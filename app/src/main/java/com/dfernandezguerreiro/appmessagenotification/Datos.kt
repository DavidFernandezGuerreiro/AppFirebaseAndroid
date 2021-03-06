package com.dfernandezguerreiro.appmessagenotification

import java.util.*

/**
 * Para guardar los valores que quiero introducir/actualizar en la base de datos
 * Contiene un HashMap con los datos, ya que las funciones que utilizaré necesitan como parámetro
 * un HashMap
 */
data class Datos(var hora: Date = Date(), var yakata: String="", var token: String="") { // var nombre: String = "",  var token: String = "",
    // contenedor para actualizar los datos
    val miHashMapDatos = HashMap<String, Any>()

    /**
     * Mete los datos del objeto en el HashMap
     */
    fun crearHashMapDatos() {
        //miHashMapDatos.put("token", token)
        //miHashMapDatos.put("nombre", nombre)
        miHashMapDatos.put("hora", hora)
        miHashMapDatos.put("yakata", yakata)
        miHashMapDatos.put("token", token)
    }
}