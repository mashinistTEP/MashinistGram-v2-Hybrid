package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var token: String
    private val selectedMembers = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        val titleEdit = findViewById<EditText>(R.id.groupTitle)
        val membersList = findViewById<ListView>(R.id.membersList)
        val createBtn = findViewById<Button>(R.id.createBtn)

        ApiClient.get("get_contacts.php", mapOf("token" to token)) { json ->
            runOnUiThread {
                val contacts = json?.optJSONArray("contacts")
                val items = ArrayList<String>()
                val ids = ArrayList<Int>()

                if (contacts != null && contacts.length() > 0) {
                    for (i in 0 until contacts.length()) {
                        val u = contacts.getJSONObject(i)
                        val uid = u.getInt("id")
                        val name = "${u.optString("first_name", "")} ${u.optString("last_name", "")}"
                        items.add(name)
                        ids.add(uid)
                    }

                    val adapter = object : ArrayAdapter<String>(
                        this@CreateGroupActivity,
                        android.R.layout.simple_list_item_multiple_choice,
                        items
                    ) {
                        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getView(position, convertView, parent)
                            view.findViewById<TextView>(android.R.id.text1).setTextColor(Color.WHITE)
                            return view
                        }
                    }
                    membersList.adapter = adapter
                    membersList.choiceMode = ListView.CHOICE_MODE_MULTIPLE

                    membersList.setOnItemClickListener { _, _, position, _ ->
                        val uid = ids[position]
                        if (selectedMembers.contains(uid)) {
                            selectedMembers.remove(uid)
                            membersList.setItemChecked(position, false)
                        } else {
                            selectedMembers.add(uid)
                            membersList.setItemChecked(position, true)
                        }
                    }
                }
            }
        }

        createBtn.setOnClickListener {
            val title = titleEdit.text.toString().trim()
            if (title.isEmpty()) { Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (selectedMembers.isEmpty()) { Toast.makeText(this, "Выберите участников", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val membersJson = org.json.JSONArray(selectedMembers.toList()).toString()
            ApiClient.post("create_group.php", mapOf("token" to token, "title" to title, "members" to membersJson)) { json ->
                runOnUiThread {
                    if (json != null && json.optBoolean("success")) {
                        startActivity(Intent(this, ChatActivity::class.java).putExtra("chat_id", json.getInt("chat_id")))
                        finish()
                    } else {
                        Toast.makeText(this, "Ошибка: ${json?.optString("error", "неизвестно")}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
