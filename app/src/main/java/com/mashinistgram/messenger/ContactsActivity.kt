package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ContactsActivity : AppCompatActivity() {
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        val contactList = findViewById<LinearLayout>(R.id.contactList)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        ApiClient.get("get_contacts.php", mapOf("token" to token)) { json ->
            runOnUiThread {
                contactList.removeAllViews()
                val contacts = json?.optJSONArray("contacts")
                if (contacts != null && contacts.length() > 0) {
                    for (i in 0 until contacts.length()) {
                        val u = contacts.getJSONObject(i)
                        val uid = u.getInt("id")
                        val name = "${u.optString("first_name", "")} ${u.optString("last_name", "")}"
                        val card = LinearLayout(this@ContactsActivity).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(14, 14, 14, 14)
                            setBackgroundColor(Color.parseColor("#2A2A3E"))
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 8 }
                            setOnClickListener { startChat(uid) }
                        }
                        val tv = TextView(this@ContactsActivity).apply {
                            text = "$name\n@${u.optString("username", "")}"
                            setTextColor(Color.parseColor("#FFFFFF"))
                            textSize = 15f
                        }
                        card.addView(tv)
                        contactList.addView(card)
                    }
                }
            }
        }
    }

    private fun startChat(uid: Int) {
        ApiClient.post("create_chat.php", mapOf(
            "token" to token,
            "recipient_id" to uid.toString(),
              // пустое сообщение — чат создастся, но без текста
        )) { json ->
            runOnUiThread {
                if (json != null && json.optBoolean("success")) {
                    startActivity(Intent(this, ChatActivity::class.java).putExtra("chat_id", json.getInt("chat_id")))
                }
            }
        }
    }
}
