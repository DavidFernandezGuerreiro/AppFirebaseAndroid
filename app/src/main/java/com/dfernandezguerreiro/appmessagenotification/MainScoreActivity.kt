package com.dfernandezguerreiro.appmessagenotification

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_score.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainScoreActivity : AppCompatActivity() {

    // para filtrar los logs
    val TAG = "Servicio"

    // referencia de la base de datos
    private var database: DatabaseReference? = null
    private var database2: DatabaseReference? = null

    var keys:DatabaseReference?=null

    val prueba = ArrayList<String>()

    var hola=""
    var nicks=""
    var nick=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_score)

        val intent = getIntent()
        nick = intent.getStringExtra("nick")

        if(nick!=null){
            Log.d(TAG, "RECIBIR NICK NAME -----> "+nick)
        }else{
            Log.d(TAG, "NO RECIBES NICK NAME -----!")
        }

        val dispositivos=arrayOf("HUAWEI", "UsuarioPrueba", "thl",nick)

        for (posicion in dispositivos.indices) {
            Log.d(TAG, "DISPOSITIVOS FIREBASE ---> " + dispositivos.get(posicion))
            database = FirebaseDatabase.getInstance().getReference("/dispositivos/" + dispositivos.get(posicion))


            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val post = dataSnapshot.getValue(Datos::class.java)!!
                    Log.d(TAG, "DATOS DE LA BASE DE DATOS - SCORE -> " + post.yakata) //+  post.hora.time + " "
                    hola=hola+"\n"+post.yakata //dispositivos.get(posicion)+": "+
                    nicks=nicks+"\n"+dispositivos.get(posicion)
                    Log.d(TAG,"HOLA HOLA HOLA HOLA ----> "+hola)
                    textScore1.setText(nicks)
                    textScore2.setText(hola)

                    prueba.add(post.yakata)
                    for (pos in prueba.indices) {
                        Log.d(TAG, "ARRRRRAY YAKATAAAAAA -> " + prueba.get(pos))
                        /*textScore.setText(String.format("SCORE: "+prueba.get(pos)[0]+"\n"
                            +prueba.get(pos)[1]+"\n"
                            +prueba.get(pos)[2]+"\n"))*/
                        //textScore2.setText(String.format("SCORE -> "+prueba.get(pos)))
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "ERROR CANCELACION", databaseError.toException())
                    // ...
                }
            }
            database!!.addValueEventListener(postListener)
        }
        Log.d(TAG,"2222222-HOLA HOLA HOLA HOLA ----> "+hola)

        // creas una referencia nueva base de datos
        //database = FirebaseDatabase.getInstance().getReference("/dispositivos/HUAWEI")
        //database2 = FirebaseDatabase.getInstance().getReference("/dispositivos/thl")

        // inicializo el listener para los eventos de la basededatos
        //initListener()
    }

}
