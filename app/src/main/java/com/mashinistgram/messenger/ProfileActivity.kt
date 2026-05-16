package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        window.decorView.setBackgroundColor(Color.parseColor("#F5F0FF"))

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        val editBtn = findViewById<Button>(R.id.editBtn)
        val nameTv = findViewById<TextView>(R.id.nameTv)
        val badgesLayout = findViewById<LinearLayout>(R.id.badgesLayout)
        val usernameTv = findViewById<TextView>(R.id.usernameTv)
        val emailTv = findViewById<TextView>(R.id.emailTv)
        val bioTv = findViewById<TextView>(R.id.bioTv)
        val starsTv = findViewById<TextView>(R.id.starsTv)
        val idTv = findViewById<TextView>(R.id.idTv)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        backBtn.setOnClickListener { finish() }
        editBtn.setOnClickListener { startActivity(Intent(this, EditProfileActivity::class.java)) }

        ApiClient.getUser(token) { user ->
            runOnUiThread {
                if (user != null) {
                    nameTv.text = "${user.optString("first_name", "")} ${user.optString("last_name", "")}"
                    usernameTv.text = user.optString("username", "").let { if (it.isNotEmpty()) "@$it" else "" }
                    emailTv.text = "📧 ${user.optString("email", "")}"
                    bioTv.text = user.optString("bio", "").ifEmpty { "Нет описания" }
                    starsTv.text = "⭐ ${user.optInt("stars_balance", 0)} звёзд"
                    idTv.text = "ID: ${user.optInt("id", 0)}"
                    BadgeHelper.addBadges(this@ProfileActivity, badgesLayout, user)
                }
            }
        }

        logoutBtn.setOnClickListener {
            ApiClient.post("logout.php", mapOf("token" to token)) { _ ->
                prefs.edit().clear().apply()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }
}
