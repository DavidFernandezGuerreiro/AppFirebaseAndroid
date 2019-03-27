package com.dfernandezguerreiro.appmessagenotification

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ChildEventListener
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*


class MainActivity : AppCompatActivity() {

    // para filtrar los logs
    val TAG = "Servicio"

    // referencia de la base de datos
    private var database: DatabaseReference? = null

    // Token del dispositivo
    private var FCMToken: String? = null
    // key unica creada automaticamente al añadir un child
    lateinit var misDatos : Datos
    //lateinit
    var key: String? = null
    // para actualizar los datos necesito un hash map
    val miHashMapChild = HashMap<String, Any>()

    var numClickYakata=0
    val numYakataHash=HashMap<String,Any>()

    var nick=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        // referencia a la base de datos del proyecto en firebase
        //database = FirebaseDatabase.getInstance().getReference("/dispositivos")
        database = FirebaseDatabase.getInstance().getReference("/dispositivos")

        // boton de la plantilla
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Tiempo actualizado", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            Log.d(TAG,"Actualizando datos")
            // cada vez que le damos click actualizamos tiempo
            misDatos.hora = Date()
            // Creamos el hashMap en el objeto
            misDatos.crearHashMapDatos()
            // actualizamos la base de datosw
            miHashMapChild.put(key.toString(),misDatos.miHashMapDatos)
            // actualizamos el child
            database!!.updateChildren(miHashMapChild)
        }

        recibirNickName()

        //botón RESTART GAME:
        val buttonRestart=findViewById<Button>(R.id.buttonRestart)
        buttonRestart.setVisibility(View.GONE)

        //llamo al método que hace la cuenta atrás:
        tiempoAtras(textTiempo)

        val intent = Intent(this, MainScoreActivity::class.java).apply {
            putExtra("nick",nick)
        }

        //boton SCORE:
        val buttonScore=findViewById<Button>(R.id.buttonScore)
        buttonScore.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "SCORE SCORE")
                startActivity(intent)
            }
        })


        //boton YAKATA:
        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "YAKATA YAKATA")
                //llamo al método para cambiar el texto el textView
                cambiarTexto(miText)
            }
        })

        // solo lo llamo cuando arranco la app
        // evito que cuando se pasa por el onCreate vuelva a ejecutarse
        if (savedInstanceState == null) {
            try {
                // Obtengo el token del dispositivo.
                key = nick //android.os.Build.MANUFACTURER //FirebaseInstanceId.getInstance().token
                // creamos una entrada nueva en el child "dispositivos" con un key unico automatico
                //key = database!!.push().key!!
                //FCMToken= database!!.push().toString()
                FCMToken = FirebaseInstanceId.getInstance().token
                // guardamos el token, dispositivo, tiempo actual en la data class
                misDatos = Datos(Date(),"",FCMToken.toString()) //android.os.Build.MANUFACTURER+" "+FCMToken.toString(),
                // creamos el hash map
                misDatos.crearHashMapDatos()
                // guardamos los datos en el hash map para la key creada anteriormente
                miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
                // actualizamos el child
                database!!.updateChildren(miHashMapChild)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "Error escribiendo datos ${e}")
            }
        }
        // inicializo el listener para los eventos de la basededatos
        initListener()
    }

    fun recibirNickName(){
        val intent = getIntent()
        nick = intent.getStringExtra("NickName")

        if(nick!=null){
            Log.d(TAG, "RECIBIR NICK NAME -----> "+nick)
        }else{
            Log.d(TAG, "NO RECIBES NICK NAME -----!")
        }
    }

    fun tiempoAtras(t: TextView){
        //val formato= SimpleDateFormat("HH:mm:ss");
        //val fechaActual= Calendar.getInstance().getTime();
        //val s=formato.format(fechaActual);
        //t.setText(String.format("Cuenta atrás: %s",s))
        val countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                t.setText(String.format(Locale.getDefault(), "%d sec.", millisUntilFinished / 1000L))
            }
            override fun onFinish() {
                t.setText("Done.")
                button.setVisibility(View.GONE);
                buttonRestart.setVisibility(View.VISIBLE)
                buttonRestart.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        Log.d(TAG, "RESTART GAME")
                        buttonRestart.setVisibility(View.GONE)
                        numClickYakata=-1
                        cambiarTexto(miText)
                        button.setVisibility(View.VISIBLE)
                        tiempoAtras(textTiempo)
                        FCMToken = FirebaseInstanceId.getInstance().token
                        misDatos = Datos(Date(),"",FCMToken.toString())
                        misDatos.crearHashMapDatos()
                        miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
                        database!!.updateChildren(miHashMapChild)
                    }
                })
            }
        }.start()

    }

    //funcion cambiarTexto, llamada en el button
    fun cambiarTexto(v: TextView){
        numClickYakata=numClickYakata+1
        v.setText(String.format("Botón presionado: %s",numClickYakata));
        misDatos.yakata=numClickYakata.toString()
        misDatos.crearHashMapDatos()
        numYakataHash.put(key.toString(),misDatos.miHashMapDatos)
        database!!.updateChildren(numYakataHash)
        Log.d(TAG,numClickYakata.toString())
    }

    /**
     * Listener para los distintos eventos de la base de datos
     */
    private fun initListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(TAG, "Datos borrados: " + p0.key)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // creo un objeto para recojer los datos cambiados
                var misDatosCambiados = Datos(Date(),"")
                // introduzco en el objeto los datos cambiados que vienen en el snapdhot
                misDatosCambiados = p0.getValue(Datos::class.java)!!
                // muestro datos desde el objeto
                Log.d(TAG, "Datos cambiados: "+  misDatosCambiados.hora.time + " "+misDatosCambiados.yakata)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "Datos movidos")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // onChildAdded() capturamos la key
                Log.d(TAG, "Datos añadidos: " + p0.key)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Error cancelacion")
            }
        }

        // attach el evenListener a la basededatos
        database!!.addChildEventListener(childEventListener)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}
