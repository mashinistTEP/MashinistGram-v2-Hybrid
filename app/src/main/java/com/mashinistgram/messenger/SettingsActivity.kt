package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {
    private var user: JSONObject? = null
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        val items = mutableListOf("⭐ Звёзды", "👑 Премиум", "🛒 Магазин", "✅ Верификация", "💎 Спонсор", "🗑️ Очистить кэш", "🔄 Сменить аккаунт", "ℹ️ О приложении")
        if (DebugHelper.enabled) items.add("🐛 Debug Menu")

        val listView = findViewById<ListView>(R.id.settingsList)
        listView.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                view.findViewById<TextView>(android.R.id.text1).apply {
                    setTextColor(Color.WHITE); textSize = 16f; setPadding(16, 14, 16, 14)
                }
                return view
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = items[position]
            when {
                selected.startsWith("⭐") -> startActivity(Intent(this, StarsActivity::class.java))
                selected.startsWith("👑") -> startActivity(Intent(this, PremiumActivity::class.java))
                selected.startsWith("🛒") -> startActivity(Intent(this, ShopActivity::class.java))
                selected.startsWith("✅") -> {
                    val msg = when {
                        user == null -> "Загрузка..."
                        user!!.optBoolean("is_creator") -> "Это создатель 👑"
                        user!!.optBoolean("verified") -> "Верифицирован ✅"
                        else -> "Не верифицирован"
                    }
                    AlertDialog.Builder(this).setTitle("Верификация").setMessage(msg).setPositiveButton("OK", null).show()
                }
                selected.startsWith("💎") -> AlertDialog.Builder(this).setTitle("Спонсор").setMessage(BadgeHelper.sponsorText(user)).setPositiveButton("OK", null).show()
                selected.startsWith("🗑️") -> AlertDialog.Builder(this).setTitle("Кэш").setMessage("Кэш очищен!").setPositiveButton("OK", null).show()
                selected.startsWith("🔄") -> {
                    prefs.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                    finish()
                }
                selected.startsWith("ℹ️") -> startActivity(Intent(this, AboutActivity::class.java))
                selected.startsWith("🐛") -> showDebugMenu(prefs)
            }
        }

        ApiClient.getUser(token) { u -> user = u }
    }

    private fun showDebugMenu(prefs: android.content.SharedPreferences) {
        val items = arrayOf("Вкл/Выкл дебаг", "Полные ошибки", "Токен сессии", "Логи", "Очистить кэш", "User ID")
        AlertDialog.Builder(this)
            .setTitle("Debug Menu")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> { DebugHelper.toggle(this); recreate() }
                    1 -> { DebugHelper.toggleFullErrors(this) }
                    2 -> DebugHelper.showToken(this)
                    3 -> DebugHelper.showNetworkLog(this)
                    4 -> DebugHelper.clearCache(this)
                    5 -> {
                        val uid = prefs.getInt("user_id", 0)
                        AlertDialog.Builder(this).setTitle("User ID").setMessage("$uid").setPositiveButton("OK", null).show()
                    }
                }
            }.show()
    }
}
