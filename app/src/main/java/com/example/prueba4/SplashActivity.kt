package com.example.prueba4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val pantallaScreen = installSplashScreen()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //comprobar si hay un nombre de usuario almacenado en el SharedPreferences

        //variable de installacion de splash siempre se hace
        Thread.sleep(3000)
        pantallaScreen.setKeepOnScreenCondition{false}


        //cargamos el fichero creado
        val sharedPref = applicationContext.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        //cargamos el contenido en el usuario.
        val user = sharedPref.getString("usuario", "Usuario")

        if(user.isNullOrEmpty()){

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }else{

            if(user.equals("admins@gmail.com")){
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }

}