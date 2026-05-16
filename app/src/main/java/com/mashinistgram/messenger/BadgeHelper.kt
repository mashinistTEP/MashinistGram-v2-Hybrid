package app.mashinistgram.hybrid

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject

object BadgeHelper {
    fun addBadges(ctx: Context, layout: LinearLayout, user: JSONObject?) {
        layout.removeAllViews()
        if (user == null) return

        if (user.optBoolean("is_creator")) {
            layout.addView(badge(ctx, R.drawable.ownerverify, "Это создатель этого мессенджера 👑"))
        } else if (user.optBoolean("verified")) {
            layout.addView(badge(ctx, R.drawable.verify, "Верифицированный аккаунт ✅"))
        }

        if (user.optBoolean("has_premium")) {
            layout.addView(badge(ctx, R.drawable.premium_star, "Премиум активен ⭐"))
        }

        val sc = user.optInt("sponsor_count", 0)
        val levels = mapOf(
            1 to Pair(R.drawable.sponsor1lvl, "Спонсор (1 уровень) 🟤"),
            2 to Pair(R.drawable.sponsor2lvl, "Хороший спонсор (2 уровень) ⚪"),
            3 to Pair(R.drawable.sponsor3lvl, "Особый спонсор (3 уровень) 🟡"),
            4 to Pair(R.drawable.sponsor4lvl, "Супер спонсор (4 уровень) 💎"),
            5 to Pair(R.drawable.sponsor5lvl, "МЕГА спонсор (5 уровень) 🟢")
        )
        val (icon, title) = levels.getOrDefault(sc, Pair(R.drawable.sponsor5pluslvl, "УЛЬТРА СПОНСОР (5+ уровень) 🔷"))
        if (sc > 0) layout.addView(badge(ctx, icon, title))
    }

    fun verificationText(user: JSONObject?): String = when {
        user == null -> "Ошибка загрузки"
        user.optBoolean("is_creator") -> "Это создатель этого мессенджера 👑"
        user.optBoolean("verified") -> "Верифицированный аккаунт ✅"
        else -> "Не верифицирован"
    }

    fun sponsorText(user: JSONObject?): String {
        val sc = user?.optInt("sponsor_count", 0) ?: 0
        if (sc == 0) return "Нет спонсорства"
        val levels = mapOf(
            1 to "Спонсор (1 уровень) 🟤", 2 to "Хороший спонсор (2 уровень) ⚪",
            3 to "Особый спонсор (3 уровень) 🟡", 4 to "Супер спонсор (4 уровень) 💎",
            5 to "МЕГА спонсор (5 уровень) 🟢"
        )
        return levels.getOrDefault(sc, "УЛЬТРА СПОНСОР (5+ уровень) 🔷")
    }

    private fun badge(ctx: Context, icon: Int, msg: String): ImageView {
        return ImageView(ctx).apply {
            setImageResource(icon)
            layoutParams = LinearLayout.LayoutParams(36, 36).apply { setMargins(3, 0, 3, 0) }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener {
                AlertDialog.Builder(ctx).setTitle("Значок").setMessage(msg).setPositiveButton("OK", null).show()
            }
        }
    }
}
