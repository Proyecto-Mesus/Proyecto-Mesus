package es.cifpcarlos3.proyecto_mesus_android.data.db

import java.sql.Connection
import java.sql.DriverManager

class DatabaseHelper {

    private val url = "jdbc:mariadb://23ibgy.h.filess.io:3305/cardvault_depthhill"
    private val user = "cardvault_depthhill" 
    private val password = "cartitasepicas123"

    @Throws(Exception::class)
    fun getConnection(): Connection {
        Class.forName("org.mariadb.jdbc.Driver")
        return DriverManager.getConnection(url, user, password)
    }
}
