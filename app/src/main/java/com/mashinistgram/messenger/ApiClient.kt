package app.mashinistgram.hybrid

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private const val BASE = "https://mashinistgrammsg.atwebpages.com/api/mg"
    private val client = UnsafeHttpClient.instance

    fun get(path: String, params: Map<String, String> = emptyMap(), callback: (JSONObject?) -> Unit) {
        val url = "$BASE/$path?" + params.map { "${it.key}=${it.value}" }.joinToString("&")
        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(null) }
            override fun onResponse(call: Call, response: Response) {
                try { callback(JSONObject(response.body?.string() ?: "{}")) }
                catch (e: Exception) { callback(null) }
            }
        })
    }

    fun post(path: String, params: Map<String, String>, callback: (JSONObject?) -> Unit) {
        val body = FormBody.Builder()
        params.forEach { (k, v) -> body.add(k, v) }
        client.newCall(Request.Builder().url("$BASE/$path").post(body.build()).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(null) }
            override fun onResponse(call: Call, response: Response) {
                try { callback(JSONObject(response.body?.string() ?: "{}")) }
                catch (e: Exception) { callback(null) }
            }
        })
    }

    fun getUser(token: String, callback: (JSONObject?) -> Unit) {
        get("check_token.php", mapOf("token" to token)) { json ->
            callback(if (json != null && json.optBoolean("valid")) json.optJSONObject("user") else null)
        }
    }
}
