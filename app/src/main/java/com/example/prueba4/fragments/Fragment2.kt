package com.example.examen_v2.fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.example.prueba4.R
import com.example.recyclerview_kotlin.adapter.Item
import com.example.recyclerview_kotlin.adapter.ItemAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

/*
import com.example.recyclerview_kotlin.adapter.Item
import com.example.recyclerview_kotlin.adapter.ItemAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
*/

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment2 : Fragment() {
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
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_2, container, false)

       /*
        rv.adapter = ItemAdapter(listOf(
            Item("El Quijote", "En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo de los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor."),
            Item("El coche fantástico", "El coche fantástico es una trepidante aventura, de un hombre que no existe, en un mundo lleno de peligros. Michael Knight, un joven solitario embarcado en una cruzada para salvar la causa de los inocentes, los indefensos, los débiles, dentro de un mundo de criminales que operan al margen de la ley…"),
            Item("La moto azul", "El colucche fantástico es una trepidante aventura, de un hombre que no existe, en un mundo lleno de peligros. Michael Knight, un joven solitario embarcado en una cruzada para salvar la causa de los inocentes, los indefensos, los débiles, dentro de un mundo de criminales que operan al margen de la ley…"),
            Item("El pajaro loco", "El coche fantástico es una trepidante aventura, de un hombre que no existe, en un mundo lleno de peligros. Michael Knight, un joven solitario embarcado en una cruzada para salvar la causa de los inocentes, los indefensos, los débiles, dentro de un mundo de criminales que operan al margen de la ley…"),
            Item("El lago del mal", "El coche fantástico es una trepidante aventura, de un hombre que no existe, en un mundo lleno de peligros. Michael Knight, un joven solitario embarcado en una cruzada para salvar la causa de los inocentes, los indefensos, los débiles, dentro de un mundo de criminales que operan al margen de la ley…")
        ))
        */

        val recyView = view.findViewById(R.id.idRecyclwViewFragment2) as RecyclerView

        var lista : ArrayList<Item> = ArrayList()

        var db = Firebase.firestore
        val reservas = db.collection("actividades")

        reservas.get().addOnSuccessListener { result ->

            for(document in result)
            {
                var item = Item (document.get("titulo").toString(),
                                document.get("descripcion").toString(),
                                document.get("id").toString(),
                                document.get("src").toString()
                )

                lista.add(item)
            }
            println(lista.toString())

            recyView.adapter = ItemAdapter(lista,"actividades" )
        }

            .addOnFailureListener{ exception ->
                Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
            }

        return  view

    }


    fun añadirItem(item :Item){
    val db = Firebase.firestore
//añadimos a la reservas pues la lista.
        db.collection("reservas").document(item.id).set(item) //añadimos a la base de datos la lista
            .addOnFailureListener{ exception ->
                Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
            }

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}