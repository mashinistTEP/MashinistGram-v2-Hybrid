package app.mashinistgram.hybrid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val fn = findViewById<EditText>(R.id.fn); val ln = findViewById<EditText>(R.id.ln)
        val uname = findViewById<EditText>(R.id.uname); val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        findViewById<Button>(R.id.regBtn).setOnClickListener {
            ApiClient.post("register.php", mapOf("first_name" to fn.text.toString(), "last_name" to ln.text.toString(), "username" to uname.text.toString(), "email" to email.text.toString(), "password" to password.text.toString())) { json ->
                runOnUiThread {
                    if (json != null && json.has("token")) {
                        prefs.edit().putString("token", json.getString("token")).putInt("user_id", json.getInt("user_id")).apply()
                        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show(); finish()
                    } else { Toast.makeText(this, json?.optString("error", "Ошибка") ?: "Ошибка", Toast.LENGTH_SHORT).show() }
                }
            }
        }
        findViewById<Button>(R.id.backBtn).setOnClickListener { finish() }
    }
}
