package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""

        val query = findViewById<EditText>(R.id.query)
        val result = findViewById<LinearLayout>(R.id.result)

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            val q = query.text.toString().trim()
            if (q.isNotEmpty()) {
                ApiClient.get("search_user.php", mapOf("q" to q)) { json ->
                    runOnUiThread {
                        result.removeAllViews()
                        if (json != null && json.optBoolean("found")) {
                            val u = json.getJSONObject("user")
                            val uid = u.getInt("id")
                            val name = "${u.optString("first_name", "")} ${u.optString("last_name", "")}"
                            result.addView(TextView(this@SearchActivity).apply {
                                text = "$name\n@${u.optString("username", "")}"
                                setTextColor(Color.parseColor("#FFFFFF"))
                                textSize = 16f
                            })
                            result.addView(Button(this@SearchActivity).apply {
                                text = "Написать"
                                setTextColor(Color.WHITE)
                                setBackgroundColor(Color.parseColor("#8A2BE2"))
                                setOnClickListener { startChat(uid) }
                            })
                        } else {
                            result.addView(TextView(this@SearchActivity).apply {
                                text = "Не найдено"
                                setTextColor(Color.parseColor("#FFFFFF"))
                            })
                        }
                    }
                }
            }
        }
    }

    private fun startChat(uid: Int) {
        // Просто переходим в чат, без авто-сообщения
        ApiClient.post("create_chat.php", mapOf(
            "token" to token,
            "recipient_id" to uid.toString(),
              // пустое сообщение — чат создастся, но без текста
        )) { json ->
            runOnUiThread {
                if (json != null && json.optBoolean("success")) {
                    startActivity(Intent(this, ChatActivity::class.java).putExtra("chat_id", json.getInt("chat_id")))
                } else {
                    Toast.makeText(this, "Ошибка создания чата", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
