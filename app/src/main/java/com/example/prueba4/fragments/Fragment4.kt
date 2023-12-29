package com.example.prueba4.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prueba4.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


import com.example.prueba4.MarkerWindow
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.adaptersv.R
//import com.example.adaptersv.mapa.MarkerWindow

import org.osmdroid.config.Configuration.*
import java.util.*

//import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment4.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment4 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//-------------------------parametros----------------------

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
        // Inflate the layout for this
        val view = inflater.inflate(R.layout.fragment_4, container, false)
//--------------------------------------------MAPA---------------------------------------
        getInstance().load(this.context, androidx.preference.
        PreferenceManager.getDefaultSharedPreferences(this.context))

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)


        /* Para situar el visor del mapa en un punto (Latitud,Longitud)*/
        val mapController = map.controller

        //Cuanto mayor sea, más grande se verá el punto elegido
        mapController.setZoom(18.0)

        //esto es un hotel
        val startPoint = GeoPoint(37.1376, -1.8289);
        mapController.setCenter(startPoint);

        colocarMarcador()

        /* MARCADORES */
        val firstMarker = Marker(map)

        var geoPoint = GeoPoint(37.1379, -1.8299)

        firstMarker.position = geoPoint
        firstMarker.icon = ContextCompat.getDrawable(this.requireContext(), R.drawable.img_compartir)
        firstMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        firstMarker.title = "Piscina"

        var infoWindow = MarkerWindow(map, "Piscina", "descripcion")
        firstMarker.infoWindow = infoWindow

        //Añade el marcador al mapa
        map.overlays.add(firstMarker)
        //Actualiza el mapa
        map.invalidate()


        val secondMarker = Marker(map)

        geoPoint = GeoPoint(37.15644086790343, -1.8265386272558004)

        secondMarker.position = geoPoint
        secondMarker.icon = ContextCompat.getDrawable(this.requireContext(), R.drawable.img_map)
        secondMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        secondMarker.title = "Bar"

        infoWindow = MarkerWindow(map, "Bar", "descripcion")
        secondMarker.infoWindow = infoWindow

        //Añade el marcador al mapa
        map.overlays.add(secondMarker)
        //Actualiza el mapa
        map.invalidate()





        //----------------------------------PERMISOS-----------------------------


        /** Solicitamos los permisos **/
        //handle permissions first, before map is created. not depicted here
        requestPermissionsIfNecessary(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION))



//-----------------------------------------------------------------------------------------
        return view
    }


    fun colocarMarcador(){

         var min :Float = -0.00020f
        val max =0.00020f
        val rango = max -min
        var latitudAl : Float =0.0f
        var longitudAl =0.0f

        var sumatorio =0

        var db = Firebase.firestore
        val actividades = db.collection("actividades")


     actividades.get().addOnSuccessListener { result ->
            for(document in result){
                val marcador = Marker(map)
                val geoPoint = GeoPoint(37.1376+(max*sumatorio),-1.8289+(max*sumatorio))

            sumatorio ++

                val titulo = document.get("titulo").toString()
                val descripcion = document.get("descripcion").toString()

                marcador.position = geoPoint
                marcador.icon = ContextCompat.getDrawable(this.requireContext(), R.drawable.img_map)
                marcador.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                marcador.title = titulo

                var infoWindow = MarkerWindow(map, titulo, descripcion)
                marcador.infoWindow = infoWindow

                //Añade el marcador al mapa
                map.overlays.add(marcador)
                //Actualiza el mapa
                map.invalidate()


            }
        }

    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this.requireContext() as Activity,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<out String>)
    {
        val permissionsToRequest = ArrayList<String>();

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this.requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.size > 0)
        {
            ActivityCompat.requestPermissions(
                this.requireContext() as Activity,
                permissionsToRequest.toArray(permissions),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        else
        {
            /* El permiso de localización ya está concedido */

            //MyLocation
            val overlay: MyLocationNewOverlay = MyLocationNewOverlay(map)
            overlay.enableFollowLocation();
            overlay.enableMyLocation();
            map.overlays.add(overlay);
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment4.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment4().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}