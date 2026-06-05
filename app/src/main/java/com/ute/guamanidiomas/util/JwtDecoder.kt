package com.ute.guamanidiomas.util

import android.util.Base64
import org.json.JSONObject

object JwtDecoder {
    fun getClaims(token: String): JSONObject? {
        try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            
            // La segunda parte del token es el Payload (los claims)
            val payload64 = parts[1]
            val bytes = Base64.decode(payload64, Base64.DEFAULT)
            val jsonString = String(bytes, charset("UTF-8"))
            
            return JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
