package app.mashinistgram.hybrid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val fn = findViewById<EditText>(R.id.fn); val ln = findViewById<EditText>(R.id.ln)
        val uname = findViewById<EditText>(R.id.uname); val bio = findViewById<EditText>(R.id.bio)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        ApiClient.getUser(token) { u -> runOnUiThread {
            fn.setText(u?.optString("first_name", "")); ln.setText(u?.optString("last_name", ""))
            uname.setText(u?.optString("username", "")); bio.setText(u?.optString("bio", ""))
        }}
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            ApiClient.post("profile.php", mapOf("token" to token, "first_name" to fn.text.toString(), "last_name" to ln.text.toString(), "username" to uname.text.toString(), "bio" to bio.text.toString())) { _ ->
                runOnUiThread { Toast.makeText(this, "Сохранено!", Toast.LENGTH_SHORT).show(); finish() }
            }
        }
    }
}
