package com.bruitage.app.security

import android.content.Context
import android.provider.Settings
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Verrouille l'appli à une tablette précise. Le code d'activation attendu est un
 * HMAC-SHA256(secret, identifiant Android) tronqué à 8 caractères hexadécimaux,
 * généré hors-ligne par un outil séparé (voir le générateur privé fourni à côté du
 * projet). La même clé secrète doit rester identique ici et dans cet outil.
 */
object ActivationManager {

    private const val SECRET_KEY = "b7f2b9e1-4d6a-4c8e-9f21-7a5d3c8e2b40-bruitage-secret"
    private const val PREFS_NAME = "activation"
    private const val KEY_ACTIVATED = "activated"

    fun deviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "inconnu"
    }

    fun formatForDisplay(rawId: String): String =
        rawId.uppercase().chunked(4).joinToString("-")

    fun isActivated(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ACTIVATED, false)
    }

    fun tryActivate(context: Context, enteredCode: String): Boolean {
        val entered = enteredCode.trim().uppercase().replace("-", "").replace(" ", "")
        val expected = expectedCode(deviceId(context)).replace("-", "")
        val ok = entered.isNotEmpty() && entered == expected
        if (ok) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_ACTIVATED, true)
                .apply()
        }
        return ok
    }

    private fun canonicalId(rawId: String): String = rawId.trim().lowercase()

    private fun expectedCode(rawDeviceId: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val hash = mac.doFinal(canonicalId(rawDeviceId).toByteArray(Charsets.UTF_8))
        val hex = hash.joinToString("") { "%02X".format(it) }
        val code = hex.take(8)
        return "${code.take(4)}-${code.takeLast(4)}"
    }
}
