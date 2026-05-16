package app.mashinistgram.hybrid

import android.graphics.*
import android.widget.ImageView

object AvatarHelper {
    private val colors = arrayOf(
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
        "#DDA0DD", "#98D8C8", "#F7DC6B", "#BB8FCE", "#85C1E9"
    )

    fun setAvatar(imageView: ImageView, name: String, size: Int = 48) {
        try {
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifEmpty { "?" }

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val color = Color.parseColor(colors[name.hashCode().and(0x7FFFFFFF) % colors.size])

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
            }
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = Color.WHITE
                textSize = size / 2.5f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            val yOffset = (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(initials, size / 2f, size / 2f - yOffset, textPaint)

            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // fallback
        }
    }
}
