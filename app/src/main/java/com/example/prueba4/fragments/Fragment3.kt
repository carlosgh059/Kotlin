package com.example.examen_v2.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba4.R
import com.example.recyclerview_kotlin.adapter.Item
import com.example.recyclerview_kotlin.adapter.ItemAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.osmdroid.views.MapView

/*
import com.example.recyclerview_kotlin.adapter.Item
import com.example.recyclerview_kotlin.adapter.ItemAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
*/

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment3 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //------------------parametros------------------------
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private lateinit var map : MapView


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

        var args = arguments
        val valuess = args?.getString("key")
        Toast.makeText(context, valuess, Toast.LENGTH_SHORT)
        println(valuess)

        // Inflate the layout for this
        val view = inflater.inflate(R.layout.fragment_3, container, false)

        val recyView = view.findViewById(R.id.idRecyclwViewFragment3) as RecyclerView

        var lista : ArrayList<Item> = ArrayList()

        var db = Firebase.firestore
        val reservas = db.collection("reservas")

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

            recyView.adapter = ItemAdapter(lista,"reservas")
        }

            .addOnFailureListener{ exception ->
                Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
            }



        return view
    }





    fun borrarItem(item : Item){

        var db =Firebase.firestore
        //con esto borramos de la listas
        db.collection("reservas").document(item.id).delete()
            .addOnFailureListener{ exception ->
                Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
            }

        db.collection("reservas")
            .whereEqualTo("state", "CA")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.d("reservas", "New city: ${dc.document.data}")
                        DocumentChange.Type.MODIFIED -> Log.d("reservas", "Modified city: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d("reservas", "Removed city: ${dc.document.data}")
                    }
                }
            }



    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}