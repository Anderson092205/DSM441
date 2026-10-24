package com.example.sqliteapp

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHash {
    private const val ITERACIONES = 120000
    fun crear(password: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return "pbkdf2_sha1:$ITERACIONES:${hex(sal)}:${hex(derivar(password, sal, ITERACIONES))}"
    }
    fun verificar(password: String, almacenado: String): Boolean = try {
        val partes = almacenado.split(':')
        if (partes.size != 4 || partes[0] != "pbkdf2_sha1") false
        else MessageDigest.isEqual(bytes(partes[3]), derivar(password, bytes(partes[2]), partes[1].toInt()))
    } catch (_: IllegalArgumentException) { false }
    private fun derivar(password: String, sal: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), sal, iteraciones, 256)
        return try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded }
        finally { spec.clearPassword() }
    }
    private fun hex(datos: ByteArray) = datos.joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun bytes(hex: String): ByteArray {
        require(hex.length % 2 == 0)
        return ByteArray(hex.length / 2) { hex.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
    }
}
