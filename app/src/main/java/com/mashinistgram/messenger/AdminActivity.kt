package app.mashinistgram.hybrid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        findViewById<ImageButton>(R.id.backBtn)?.setOnClickListener { finish() }
        Toast.makeText(this, "Admin opened", Toast.LENGTH_SHORT).show()
    }
}
