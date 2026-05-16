package app.mashinistgram.hybrid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StarsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        ApiClient.getUser(prefs.getString("token", "") ?: "") { u ->
            runOnUiThread { findViewById<TextView>(R.id.starsBalance).text = "У вас ${u?.optInt("stars_balance", 0) ?: 0} ⭐" }
        }
    }
}
