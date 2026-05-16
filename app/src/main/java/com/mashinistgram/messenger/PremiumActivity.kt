package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PremiumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<Button>(R.id.shopBtn).setOnClickListener { startActivity(Intent(this, ShopActivity::class.java)) }

        val features = listOf("Звёздочка ⭐ у ника", "Жирный фиолетовый ник 💜", "Приоритетная поддержка 🎧", "Уникальные стикеры 🎨", "Особые реакции 🔥")
        val fl = findViewById<LinearLayout>(R.id.featuresLayout)
        for (f in features) fl.addView(TextView(this).apply { text = "❌ $f"; setTextColor(Color.parseColor("#888888")); textSize = 15f; setPadding(0, 6, 0, 6) })
        ApiClient.getUser(prefs.getString("token", "") ?: "") { u ->
            runOnUiThread {
                findViewById<TextView>(R.id.statusTv).text = "Премиум ${if (u?.optBoolean("has_premium", false) == true) "активен ✅" else "не активен"}"
                if (u?.optBoolean("has_premium", false) == true) {
                    fl.removeAllViews()
                    for (f in features) fl.addView(TextView(this).apply { text = "✅ $f"; setTextColor(Color.parseColor("#1A1A2E")); textSize = 15f; setPadding(0, 6, 0, 6) })
                }
            }
        }
    }
}
