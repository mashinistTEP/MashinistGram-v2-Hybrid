package app.mashinistgram.hybrid

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var userId = 0
    private var chatId = 0
    private lateinit var token: String
    private lateinit var msgList: LinearLayout
    private lateinit var input: EditText
    private var isSending = false
    private var editingMsgId = 0
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        userId = prefs.getInt("user_id", 0)
        chatId = intent.getIntExtra("chat_id", 0)
        if (chatId == 0) { finish(); return }
        msgList = findViewById(R.id.msgList)
        input = findViewById(R.id.input)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.sendBtn).setOnClickListener { sendMessage() }

        // Помечаем сообщения прочитанными
        ApiClient.post("mark_read.php", mapOf("token" to token, "chat_id" to chatId.toString())) { _ -> }
        
        loadMessages()

        // Авто-обновление каждые 3 секунды
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() { loadMessages() }
        }, 3000, 3000)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    private fun loadMessages() {
        ApiClient.get("get_messages.php", mapOf("token" to token, "chat_id" to chatId.toString(), "limit" to "50")) { json ->
            runOnUiThread {
                msgList.removeAllViews()
                val messages = json?.optJSONArray("messages")
                if (messages != null) for (i in 0 until messages.length()) {
                    val msg = messages.getJSONObject(i)
                    val mine = msg.getInt("sender_id") == userId
                    val sender = if (mine) "Я" else msg.optString("first_name", "")
                    val status = msg.optString("status", "sent")
                    val time = msg.optString("created_at", "").takeLast(8)
                    val msgId = msg.getInt("id")

                    val row = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = if (mine) Gravity.END else Gravity.START
                    }

                    val bubble = TextView(this).apply {
                        text = "$sender: ${msg.optString("text", "")}"
                        setTextColor(if (mine) Color.WHITE else Color.parseColor("#FFFFFF"))
                        setPadding(20, 14, 20, 14)
                        textSize = 16f
                        maxWidth = (resources.displayMetrics.widthPixels * 0.9).toInt()
                        val bg = GradientDrawable().apply {
                            setColor(if (mine) Color.parseColor("#8A2BE2") else Color.parseColor("#2A2A3E"))
                            cornerRadius = 22f
                        }
                        background = bg
                    }

                    if (mine) {
                        bubble.setOnClickListener {
                            val items = arrayOf("Удалить", "Изменить")
                            AlertDialog.Builder(this@ChatActivity)
                                .setTitle("Сообщение")
                                .setItems(items) { _, which ->
                                    when (which) {
                                        0 -> deleteMessage(msgId)
                                        1 -> startEdit(msgId, msg.optString("text", ""))
                                    }
                                }.show()
                        }
                    }

                    row.addView(bubble)

                    val infoRow = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = if (mine) Gravity.END else Gravity.START
                    }
                    val timeTv = TextView(this).apply {
                        text = time
                        setTextColor(Color.parseColor("#888888"))
                        textSize = 11f
                        setPadding(8, 2, 4, 0)
                    }
                    infoRow.addView(timeTv)

                    if (mine) {
                        val animView = LottieAnimationView(this@ChatActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(18, 18)
                            when (status) {
                                "sending" -> setAnimation(R.raw.timer)
                                "sent" -> setAnimation(R.raw.ticks_single)
                                "delivered" -> setAnimation(R.raw.ticks_double)
                                else -> setAnimation(R.raw.timer)
                            }
                            playAnimation()
                        }
                        infoRow.addView(animView)
                    }
                    row.addView(infoRow)
                    msgList.addView(row, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 12 })
                }
                isSending = false
            }
        }
    }

    private fun startEdit(msgId: Int, oldText: String) {
        editingMsgId = msgId
        input.setText(oldText)
        input.requestFocus()
        input.setSelection(oldText.length)
    }

    private fun deleteMessage(msgId: Int) {
        ApiClient.post("delete_message.php", mapOf("token" to token, "message_id" to msgId.toString())) { _ ->
            runOnUiThread { loadMessages() }
        }
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty() || isSending) return
        isSending = true
        if (editingMsgId > 0) {
            ApiClient.post("edit_message.php", mapOf("token" to token, "message_id" to editingMsgId.toString(), "text" to text)) { _ ->
                runOnUiThread { input.text.clear(); editingMsgId = 0; loadMessages() }
                runOnUiThread { input.text.clear(); editingMsgId = 0; loadMessages() }
            }
        } else {
            ApiClient.post("send_message.php", mapOf("token" to token, "chat_id" to chatId.toString(), "text" to text)) { _ ->
                runOnUiThread { input.text.clear(); loadMessages() }
            }
        }
    }
}
