package app.mashinistgram.hybrid

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        val version = packageManager.getPackageInfo(packageName, 0).versionName ?: "0.1.0"
        findViewById<TextView>(R.id.versionTv).text = version
        findViewById<TextView>(R.id.versionTvEn).text = version

        val debugListener = android.view.View.OnClickListener {
            clickCount++
            if (clickCount >= 5) {
                DebugHelper.toggle(this)
                if (DebugHelper.enabled) {
                    Toast.makeText(this, "Debug Menu включён! Зайдите в настройки.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Debug Menu выключен.", Toast.LENGTH_SHORT).show()
                }
                clickCount = 0
            }
        }
        findViewById<TextView>(R.id.versionTv).setOnClickListener(debugListener)
        findViewById<TextView>(R.id.versionTvEn).setOnClickListener(debugListener)

        findViewById<TextView>(R.id.licenseTitleRu).setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }
        findViewById<TextView>(R.id.licenseTitleEn).setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }
    }
}
