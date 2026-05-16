package app.mashinistgram.hybrid

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

object TelegramClient {
    private const val API_ID = 30417822
    private const val API_HASH = "45c2d1f70095d1c2189e722232d6bed8"
    private const val TG_API = "https://api.telegram.org"
    private val client = UnsafeHttpClient.instance

    // Авторизация по номеру телефона
    fun sendCode(phone: String, callback: (String?) -> Unit) {
        val body = FormBody.Builder()
            .add("phone", phone)
            .add("api_id", API_ID.toString())
            .add("api_hash", API_HASH)
            .build()
        client.newCall(Request.Builder().url("$TG_API/auth.sendCode").post(body).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(null) }
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "{}")
                callback(json.optString("phone_code_hash", null))
            }
        })
    }

    // Подтверждение кода
    fun signIn(phone: String, code: String, phoneCodeHash: String, callback: (String?) -> Unit) {
        val body = FormBody.Builder()
            .add("phone", phone)
            .add("phone_code", code)
            .add("phone_code_hash", phoneCodeHash)
            .add("api_id", API_ID.toString())
            .add("api_hash", API_HASH)
            .build()
        client.newCall(Request.Builder().url("$TG_API/auth.signIn").post(body).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(null) }
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val userId = json.optString("user_id", null)
                callback(userId)
            }
        })
    }

    // Отправка сообщения
    fun sendMessage(chatId: String, text: String, callback: (Boolean) -> Unit) {
        val body = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", text)
            .build()
        client.newCall(Request.Builder().url("$TG_API/sendMessage").post(body).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(false) }
            override fun onResponse(call: Call, response: Response) {
                callback(true)
            }
        })
    }

    // Получение сообщений (заглушка — нужен MTProto для real-time)
    fun getMessages(chatId: String, limit: Int = 50, callback: (JSONObject?) -> Unit) {
        val url = "$TG_API/getUpdates?chat_id=$chatId&limit=$limit"
        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(null) }
            override fun onResponse(call: Call, response: Response) {
                callback(JSONObject(response.body?.string() ?: "{}"))
            }
        })
    }
}
