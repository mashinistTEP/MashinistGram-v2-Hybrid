package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatsActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var chatList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        if (token.isEmpty()) { startActivity(Intent(this, LoginActivity::class.java)); finish(); return }
        chatList = findViewById(R.id.chatList)
        findViewById<ImageButton>(R.id.searchBtn).setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }

        val createBtn = findViewById<LottieAnimationView>(R.id.createBtn)
        createBtn.setAnimation(R.raw.create)
        createBtn.playAnimation()
        createBtn.setOnClickListener {
            val items = arrayOf("Создать группу", "Создать канал")
            android.app.AlertDialog.Builder(this)
                .setTitle("Создать")
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> startActivity(Intent(this, CreateGroupActivity::class.java))
                        1 -> Toast.makeText(this, "Каналы скоро!", Toast.LENGTH_SHORT).show()
                    }
                }.show()
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> true
                R.id.nav_contacts -> { startActivity(Intent(this, ContactsActivity::class.java)); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }
        loadChats()
    }

    override fun onResume() { super.onResume(); loadChats() }

    private fun loadChats() {
        ApiClient.get("get_chats.php", mapOf("token" to token)) { json ->
            runOnUiThread {
                val chats = json?.optJSONArray("chats")
                val items = ArrayList<org.json.JSONObject>()
                if (chats != null && chats.length() > 0) {
                    for (i in 0 until chats.length()) items.add(chats.getJSONObject(i))
                }
                chatList.adapter = ChatAdapter(items)
                chatList.setOnItemClickListener { _, _, pos, _ ->
                    if (pos < items.size) {
                        val cid = items[pos].getInt("id")
                        startActivity(Intent(this@ChatsActivity, ChatActivity::class.java).putExtra("chat_id", cid))
                    }
                }
            }
        }
    }

    inner class ChatAdapter(private val chats: List<org.json.JSONObject>) : BaseAdapter() {
        override fun getCount() = if (chats.isEmpty()) 1 else chats.size
        override fun getItem(pos: Int) = if (chats.isEmpty()) null else chats[pos]
        override fun getItemId(pos: Int) = pos.toLong()

        override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(this@ChatsActivity).inflate(R.layout.item_chat, parent, false)
            val avatar = view.findViewById<ImageView>(R.id.avatar)
            val nameTv = view.findViewById<TextView>(R.id.chatName)
            val msgTv = view.findViewById<TextView>(R.id.lastMsg)

            if (chats.isEmpty()) {
                nameTv.text = "Нет чатов"
                msgTv.text = "Нажмите + для создания"
            } else {
                val chat = chats[pos]
                val withUser = chat.optJSONObject("with_user")
                val name = if (chat.optString("chat_type") == "group") {
                    chat.optString("title", "Группа")
                } else {
                    withUser?.optString("first_name", "") + " " + withUser?.optString("last_name", "")
                }
                nameTv.text = name
                msgTv.text = chat.optString("last_message", "")
                AvatarHelper.setAvatar(avatar, name)
            }
            return view
        }
    }
}
