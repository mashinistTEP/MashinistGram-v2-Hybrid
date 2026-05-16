package app.mashinistgram.hybrid

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object DebugHelper {
    private const val TAG = "MashinistGram"
    var enabled = false
    var showFullErrors = false
    private var logFile: File? = null

    fun init(ctx: Context) {
        val prefs = ctx.getSharedPreferences("debug", Context.MODE_PRIVATE)
        enabled = prefs.getBoolean("debug_enabled", false)
        showFullErrors = prefs.getBoolean("show_full_errors", false)
        if (enabled) logFile = File(ctx.getExternalFilesDir(null), "debug.log")
    }

    fun toggle(ctx: Context): Boolean {
        enabled = !enabled
        ctx.getSharedPreferences("debug", Context.MODE_PRIVATE).edit().putBoolean("debug_enabled", enabled).apply()
        if (enabled) {
            logFile = File(ctx.getExternalFilesDir(null), "debug.log")
            Toast.makeText(ctx, "Дебаг ВКЛ", Toast.LENGTH_SHORT).show()
        } else {
            logFile = null
            Toast.makeText(ctx, "Дебаг ВЫКЛ", Toast.LENGTH_SHORT).show()
        }
        return enabled
    }

    fun toggleFullErrors(ctx: Context): Boolean {
        showFullErrors = !showFullErrors
        ctx.getSharedPreferences("debug", Context.MODE_PRIVATE).edit().putBoolean("show_full_errors", showFullErrors).apply()
        Toast.makeText(ctx, if (showFullErrors) "Полные ошибки ВКЛ" else "Полные ошибки ВЫКЛ", Toast.LENGTH_SHORT).show()
        return showFullErrors
    }

    fun log(message: String) {
        Log.d(TAG, message)
        if (enabled && logFile != null) {
            try {
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                FileWriter(logFile, true).append("$time $message\n").close()
            } catch (_: Exception) {}
        }
    }

    fun showToken(ctx: Context) {
        val prefs = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "не найден") ?: "не найден"
        AlertDialog.Builder(ctx).setTitle("Токен").setMessage(token).setPositiveButton("OK", null).show()
    }

    fun showNetworkLog(ctx: Context) {
        if (logFile != null && logFile!!.exists()) {
            val text = logFile!!.readText().takeLast(2000)
            AlertDialog.Builder(ctx).setTitle("Логи").setMessage(text.ifEmpty { "Пусто" }).setPositiveButton("OK", null).show()
        } else {
            Toast.makeText(ctx, "Логи пусты", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearCache(ctx: Context) {
        try {
            val cacheDir = ctx.cacheDir
            cacheDir.deleteRecursively()
            Toast.makeText(ctx, "Кэш очищен!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(ctx, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
