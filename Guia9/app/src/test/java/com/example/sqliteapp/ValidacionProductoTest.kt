package com.example.sqliteapp

import org.junit.Assert.*
import org.junit.Test

class ValidacionProductoTest {
    @Test fun precioAceptaDecimalesYRechazaValoresFueraDeRango() {
        assertEquals(2.5, ValidacionProducto.precio("2.50")!!, 0.0)
        assertEquals(2.5, ValidacionProducto.precio("2,50")!!, 0.0)
        assertEquals(0.0, ValidacionProducto.precio("0")!!, 0.0)
        for (valor in listOf("", "-1", "NaN", "1.234", "100000000", "1e3"))
            assertNull(valor, ValidacionProducto.precio(valor))
    }
    @Test fun codigosYCantidadesDistinguenCeroYControlanDesbordamientos() {
        assertEquals(0, ValidacionProducto.cantidad("0"))
        assertNull(ValidacionProducto.codigo("0"))
        assertEquals(12, ValidacionProducto.codigo("12"))
        for (valor in listOf("-1", "1.5", "2147483648", "abc")) {
            assertNull(ValidacionProducto.cantidad(valor))
            assertNull(ValidacionProducto.codigo(valor))
        }
    }
    @Test fun contrasenasSeGuardanConSalYSeVerificanSinTextoPlano() {
        val primero = PasswordHash.crear("Guia09#2026")
        val segundo = PasswordHash.crear("Guia09#2026")
        assertNotEquals(primero, segundo)
        assertFalse(primero.contains("Guia09#2026"))
        assertTrue(PasswordHash.verificar("Guia09#2026", primero))
        assertFalse(PasswordHash.verificar("otra", primero))
        assertFalse(PasswordHash.verificar("clave", "dato invalido"))
    }
}
