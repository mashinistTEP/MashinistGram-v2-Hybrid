package app.mashinistgram.hybrid

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        val logFile = File(context.getExternalFilesDir(null), "crash.log")
        try {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            logFile.appendText("=== Crash ${System.currentTimeMillis()} ===\n${sw}\n\n")
        } catch (_: Exception) {}

        if (DebugHelper.showFullErrors) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val activity = context as? android.app.Activity
            activity?.runOnUiThread {
                AlertDialog.Builder(context)
                    .setTitle("Полная ошибка")
                    .setMessage(sw.toString())
                    .setPositiveButton("OK") { _, _ -> activity.finish() }
                    .show()
            }
        } else {
            defaultHandler?.uncaughtException(t, e)
        }
    }
}
