package app.mashinistgram.hybrid

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
    }
}
