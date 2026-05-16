package app.mashinistgram.hybrid

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Создаём папку приложения при первом запуске
        try {
            val dir = getExternalFilesDir(null)
            if (dir != null && !dir.exists()) dir.mkdirs()
            val testFile = File(dir, "app_init.txt")
            if (!testFile.exists()) testFile.createNewFile()
        } catch (_: Exception) {}

        DebugHelper.init(this)
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
        // Создаём краш-лог
        try {
            val logFile = java.io.File(getExternalFilesDir(null), "crash.log")
            if (!logFile.exists()) logFile.createNewFile()
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                logFile.appendText("Crash: ${e.message}\n${e.stackTraceToString()}\n\n")
            }
        } catch (_: Exception) {}

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val savedToken = prefs.getString("token", "")
        if (!savedToken.isNullOrEmpty()) {
            ApiClient.getUser(savedToken) { user ->
                if (user != null) { startActivity(Intent(this, ChatsActivity::class.java)); finish() }
            }
            return
        }
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            ApiClient.post("login.php", mapOf("email" to email.text.toString(), "password" to password.text.toString())) { json ->
                runOnUiThread {
                    if (json != null && json.has("token")) {
                        prefs.edit().putString("token", json.getString("token")).putInt("user_id", json.getInt("user_id")).apply()
                        startActivity(Intent(this, ChatsActivity::class.java)); finish()
                    } else { Toast.makeText(this, json?.optString("error", "Ошибка") ?: "Ошибка", Toast.LENGTH_SHORT).show() }
                }
            }
        }
        findViewById<Button>(R.id.registerBtn).setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}
