package com.example.prueba4

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
//import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.examen_v2.fragments.Fragment1
import com.example.examen_v2.fragments.Fragment2
import com.example.examen_v2.fragments.Fragment3
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba4.fragments.Fragment4
import com.example.prueba4.fragments.Fragment5
import com.example.recyclerview_kotlin.adapter.ItemAdapter


class MainActivity : AppCompatActivity() {



    private lateinit var searchView: SearchView
    private var activadoSearch = false
    lateinit var adapter:ItemAdapter
    lateinit var rv: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //cargamos el fragmento 1.KT pero de la carpeta java, no es el .XML,
        cargarFragment(Fragment1())
        activadoSearch=false
        //recogemos el menuNavegacion de abajo
        val menuAbajo = findViewById(R.id.bottomNavigationView) as BottomNavigationView

        val topAppBar: MaterialToolbar = findViewById(R.id.toolbar)
        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Iconos -> {
                    Toast.makeText(baseContext,"Cerrando sesion Correctamente", Toast.LENGTH_SHORT).show()

                    //crear el fichero llamado usuarios
                    val sharedPref = applicationContext
                        .getSharedPreferences("usuarios", Context.MODE_PRIVATE)

                    //guardamos en el fichero el usuario.
                    with(sharedPref.edit()){
                        putString("usuario","")
                        putString("apellido","")
                        apply()
                    }

                     val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }

                R.id.mapaNuevo -> {
                    cargarFragment(Fragment5())
                    searchView.isVisible=true
                    true

                }

                else -> false
            }
        }

        menuAbajo.setOnItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.page_perfil -> {
                    // Respond to navigation item 1 reselection
                    cargarFragment(Fragment1())
                    searchView.isVisible=false
                    true
                }
                R.id.page_actividades -> {
                    // Respond to navigation item 1 reselection
                    cargarFragment(Fragment2())
                    searchView.isVisible=true
                   true
                }
                R.id.page_calendario -> {
                    // Respond to navigation item 1 reselection
                    val fragment = Fragment3()
                    val args = Bundle()
                    args.putString("key","hola")
                    fragment.arguments = args

                    val fragmentManager = supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.frameLayoutMain, fragment)
                    transaction.commit()
                 //  cargarFragment(Fragment3())


                    true
                } R.id.page_mapas -> {

                    cargarFragment(Fragment4())
                    searchView.isVisible=false
                    true
                }
                else -> false
            }
        }
        var rvs = findViewById<RecyclerView>(R.id.idRecyclwViewFragment2)
         //rv = findViewById(R.id.idRecyclwViewFragment2)  as RecyclerView
         searchView = findViewById(R.id.searchView)
         searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
           //Toast.makeText(this@MainActivity, "Buscando :  $query",Toast.LENGTH_SHORT).show()
             return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
           //Toast.makeText(this@MainActivity, "Buscando 2:  $newText",Toast.LENGTH_SHORT).show()

                adapter = rvs.adapter as ItemAdapter
                adapter.filter.filter(newText)

                return true
            }

        })

        searchView.isVisible=false






    }



    private fun cargarFragment(fragment: Fragment)
    {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.add(R.id.frameLayoutMain, fragment)
        fragmentTransition.commit()
    }



}
