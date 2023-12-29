package com.example.prueba4

import android.content.Context

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {


    private var firebaseAuth = Firebase.auth
    private var user : String =""
    private var pass : String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        //rescatamos el usuario y el pass para el FIREBASE
        val botonEntrar : Button = findViewById(R.id.button)
        val txtUsuario : TextInputEditText = findViewById(R.id.usuario)
        val txtPass : TextInputEditText = findViewById(R.id.TextodeContrasena)

        ///***leemos el usuario y contraseña****
        val textoUsuario : TextInputLayout = findViewById(R.id.textInputLayout_Usuario)
        val inputTextUsuario = textoUsuario.editText?.text.toString()
        val textoContraseña : TextInputLayout = findViewById(R.id.textInputLayout_Contraseña)
        val inputTextContraseña = textoContraseña.editText?.text.toString()


        textoUsuario.editText?.doOnTextChanged()
        { inputTextUsuario, _, _, _ ->
            user = inputTextUsuario.toString()
        }

        textoContraseña.editText?.doOnTextChanged()
        { inputTextContraseña, _, _, _ ->
            pass = inputTextContraseña.toString()
        }


        botonEntrar.setOnClickListener{

        if(!user.isNullOrEmpty() && !pass.isNullOrEmpty()){

            //crear el fichero llamado usuarios
            val sharedPref = applicationContext
                .getSharedPreferences("usuarios",Context.MODE_PRIVATE)

            //guardamos en el fichero el usuario.
            with(sharedPref.edit()){
                putString("usuario",user)
                apply()
            }

            println(txtUsuario)
            println(txtPass)
            signIn(txtUsuario.text.toString(),txtPass.text.toString())
        }else{
            Toast.makeText(baseContext,"Error campos incorrectos", Toast.LENGTH_SHORT).show()
        }


        }







    }

    private fun signIn(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task->
            if(task.isSuccessful){
              //  println("asdasd")

                if(email.equals("admins@gmail.com")){
                    val intents = Intent(this, AdminActivity::class.java)
                    startActivity(intents)
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }




           //     Toast.makeText(baseContext,"Funcionaasd", Toast.LENGTH_SHORT).show()

            }
            else{
               // println("no funcionaa")
                Toast.makeText(baseContext,"Error de Email y/o contraseña", Toast.LENGTH_SHORT).show()

            }
        }
    }




}
