package app.mashinistgram.hybrid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ShopActivity : AppCompatActivity() {
    private lateinit var token: String
    private var userStars = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }

        val starsTv = findViewById<TextView>(R.id.starsTv)
        val shopList = findViewById<ListView>(R.id.shopList)

        ApiClient.getUser(token) { user ->
            runOnUiThread {
                userStars = user?.optInt("stars_balance", 0) ?: 0
                starsTv.text = "⭐ Ваш баланс: $userStars звёзд"
            }
        }

        ApiClient.get("get_shop.php") { json ->
            runOnUiThread {
                val items = json?.optJSONArray("items")
                val itemNames = ArrayList<String>()
                val itemIds = ArrayList<Int>()
                val itemPrices = ArrayList<Int>()

                if (items != null && items.length() > 0) {
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        itemNames.add("${item.optString("item_name")} — ${item.optInt("price_stars")} ⭐\n${item.optString("description", "")}")
                        itemIds.add(item.getInt("id"))
                        itemPrices.add(item.getInt("price_stars"))
                    }

                    shopList.adapter = object : ArrayAdapter<String>(
                        this@ShopActivity, android.R.layout.simple_list_item_1, itemNames
                    ) {
                        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getView(position, convertView, parent)
                            view.findViewById<TextView>(android.R.id.text1).apply {
                                setTextColor(Color.WHITE)
                                textSize = 14f
                                setPadding(16, 12, 16, 12)
                            }
                            return view
                        }
                    }

                    shopList.setOnItemClickListener { _, _, position, _ ->
                        val itemId = itemIds[position]
                        val price = itemPrices[position]
                        val name = itemNames[position].split(" — ")[0]

                        AlertDialog.Builder(this@ShopActivity)
                            .setTitle("Покупка")
                            .setMessage("Купить $name за $price ⭐?\nВаш баланс: $userStars ⭐")
                            .setPositiveButton("Купить") { _, _ -> buyItem(itemId) }
                            .setNegativeButton("Отмена", null)
                            .show()
                    }
                }
            }
        }
    }

    private fun buyItem(itemId: Int) {
        ApiClient.post("buy_item.php", mapOf("token" to token, "item_id" to itemId.toString())) { json ->
            runOnUiThread {
                if (json != null && json.optBoolean("success")) {
                    AlertDialog.Builder(this)
                        .setTitle("Успешно")
                        .setMessage(json.optString("message", "Покупка совершена!"))
                        .setPositiveButton("OK") { _, _ -> finish() }
                        .show()
                } else {
                    val errorMsg = json?.optString("error", null) ?: "Неизвестная ошибка"
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
