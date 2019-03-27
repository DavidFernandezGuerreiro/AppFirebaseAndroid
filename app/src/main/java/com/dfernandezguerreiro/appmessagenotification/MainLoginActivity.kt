package com.dfernandezguerreiro.appmessagenotification

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_main_login.*

class MainLoginActivity : AppCompatActivity() {

    // para filtrar los logs
    val TAG = "Servicio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        //Text Nick Name:
        val nickName=findViewById<EditText>(R.id.editTextNick)
        //var nick=nickName.getText()

        /*val intent= Intent(this,MainActivity::class.java).apply {
            putExtra("NickName","texto de prueba")
        }*/
        val intent= Intent(this,MainActivity::class.java)



        //BOTÓN PLAY:
        val buttonPlay=findViewById<Button>(R.id.buttonPlay)
        buttonPlay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "ACTIVITY_LOGIN -> ACTIVITY_MAIN")
                intent.putExtra("NickName",nickName.getText().toString())
                startActivity(intent)
            }
        })

    }

}
