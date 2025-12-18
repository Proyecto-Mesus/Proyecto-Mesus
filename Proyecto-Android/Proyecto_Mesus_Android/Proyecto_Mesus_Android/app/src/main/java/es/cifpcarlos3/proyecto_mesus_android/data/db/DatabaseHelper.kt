package es.cifpcarlos3.proyecto_mesus_android.data.db

import java.sql.Connection
import java.sql.DriverManager
import android.util.Log

class DatabaseHelper {

    private val url = "jdbc:mariadb://10.0.2.2:3306/usuarios_app"
    private val user = "root" 
    private val password = ""

    @Throws(Exception::class)
    fun getConnection(): Connection {
        Class.forName("org.mariadb.jdbc.Driver")
        return DriverManager.getConnection(url, user, password)
    }
}
