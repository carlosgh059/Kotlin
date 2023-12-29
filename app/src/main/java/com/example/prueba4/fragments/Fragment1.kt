package com.example.examen_v2.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.prueba4.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //cargamos el fragmento 1, el XML lo guardamos en una varibale llamada view.

        val view  = inflater.inflate(R.layout.fragment_1, container, false)

        ///rescatamos textfield usuario
        var nombre : String =""
        var apell : String=""
        var usuario : TextInputLayout = view.findViewById(R.id.texto_input_Usuario)
        val inputTextUsuario = usuario.editText?.text.toString()
        val apellido : TextInputLayout = view.findViewById(R.id.texto_input_Apellido)
        val inputTextApellido = apellido.editText?.text.toString()

        //cargamos el fichero creado
        val sharedPref = view.context.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        //cargamos el contenido en el usuario.

        val user = sharedPref.getString("usuario", "Usuario")
        val user2 = sharedPref.getString("apellido", "apellido")

        usuario.editText?.setText(user)

        if(!user2.isNullOrEmpty()){
            apellido.editText?.setText(user2)
        }

        //recogemos el apellido y boton
         val boton : Button = view.findViewById(R.id.button2)

        boton.setOnClickListener{

            usuario.editText?.doOnTextChanged()
            { inputTextUsuario, _, _, _ ->
                nombre = inputTextUsuario.toString()
            }

            apellido.editText?.doOnTextChanged()
            { inputTextApellido, _, _, _ ->
                apell = inputTextApellido.toString()
            }

        if(!nombre.isNullOrEmpty() || !apell.isNullOrEmpty() ) {

            if(!nombre.isNullOrEmpty()) {

                //guardamos en el fichero el usuario.
                with(sharedPref.edit()){
                    putString("usuario",nombre)
                    apply()
                }

                Toast.makeText(context,"Añadido nombre", Toast.LENGTH_SHORT).show()

            }
            if(!apell.isNullOrEmpty()) {

                //guardamos en el fichero el usuario.
                with(sharedPref.edit()){
                    putString("apellido",apell)
                    apply()

                }
                Toast.makeText(context,"Añadidos Apellido", Toast.LENGTH_SHORT).show()


            }
        }else{
            Toast.makeText(context,"No se han podido añadir", Toast.LENGTH_SHORT).show()

        }




        }
        return  view



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}