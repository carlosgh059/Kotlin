package com.example.recyclerview_kotlin.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.examen_v2.fragments.Fragment2
import com.example.examen_v2.fragments.Fragment3
import com.example.prueba4.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

//import com.example.recyclerview_kotlin.R

class ItemAdapter(private val items: ArrayList<Item>,
                  private val modo: String,
                  private var context : Context? = null): RecyclerView.Adapter<ItemAdapter.ViewHolder>()
                  ,Filterable
{
    private var itemsFull : ArrayList<Item> = ArrayList<Item>()
    init
    {
        itemsFull.addAll(items)
    }

    //Obtenemos el contexto del RecyclerView
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView)

        context = recyclerView.context //Solo se haría una vez
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.vista_recycle, parent, false)

        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = items[position]
        holder.bind(item)

        //rescatamos el boton de la lista.
        val boton : FloatingActionButton = holder.itemView.findViewById(R.id.fab_fav)
        var seleccionado=false

        //rescatamos el boton enviar
        val botonEnviar : FloatingActionButton = holder.itemView.findViewById((R.id.id_enviar_1))

        botonEnviar.setOnClickListener(View.OnClickListener{
            val btnEnviar: FloatingActionButton = it as FloatingActionButton
          //  btnEnviar.setImageResource(R.drawable.img_user)
            //Acciones para compartir la actividad
            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Apúntate a la actividad: " +
                    item.titulo + "\n" + "Se realiza en: " + item.descripcion)

            context?.let { it1 -> startActivity(it1,shareIntent,null) }



        })
        if(modo=="reservas"){
            boton.setImageResource(R.drawable.img_eliminar)
        }

        boton.setOnClickListener(View.OnClickListener {
            val btn: FloatingActionButton = it as FloatingActionButton
            if(modo=="actividades"){

                if(!seleccionado){
                 seleccionado=true
                 btn.setImageResource(R.drawable.img_listo)
                 Toast.makeText(it.context,"Añadido a favoritos",Toast.LENGTH_LONG).show()
                 Fragment2().añadirItem(item)

                //Audio
                var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.aceptar)
                cuentaAtras(mediaPlayer)
               // sonidoAtras(mediaPlayer)
                }
            }
            if(modo=="reservas"){
                if(!seleccionado){
            seleccionado=true
                btn.setImageResource(R.drawable.img_listo)
                Toast.makeText(it.context,"Eliminado de favoritos",Toast.LENGTH_LONG).show()
             Fragment3().borrarItem(item)

                //Audio
                var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.rechazar)
                 cuentaAtras(mediaPlayer)
            }
            }

        })
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        private val titulo = view.findViewById<TextView>(R.id.textView_titulo)
        private val descripcion = view.findViewById<TextView>(R.id.textView_descripcion)
        private val img =view.findViewById<ImageView>(R.id.imageView)


        fun bind(item: Item)
        {
            titulo.text = item.titulo
            descripcion.text = item.descripcion

            //Cargar imagen o añadir una imagen
            Glide
                .with(itemView.context)
                .load(item.src)
                .into(img)

        }
    }

    fun cuentaAtras( cancion : MediaPlayer?){

        var countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                cancion?.start()
            }
            override fun onFinish() {
                cancion?.stop()
                cancion?.release()
            }
        }
        countDownTimer.start()

    }

    fun sonidoAtras(cancion : MediaPlayer?){


       if(cancion!=null && !cancion.isPlaying){
         Handler(Looper.getMainLooper()).postDelayed({
           cancion?.stop()
           cancion?.release()
        },3000)
        cancion?.start()

       }

    }



    //---***---searchView---***--
    //SearchView
    override fun getFilter(): Filter
    {
        return filtro
    }

    private val filtro: Filter = object : Filter()
    {
        override fun performFiltering(constraint: CharSequence): FilterResults
        {
            val listaFiltrada: ArrayList<Item> = ArrayList()

            if (constraint.isEmpty())
            {
                listaFiltrada.addAll(itemsFull)
            }
            else
            {
                val patronBusqueda = constraint.toString().lowercase()

                for (item in itemsFull)
                {
                    if (item.titulo.lowercase().contains(patronBusqueda))
                    {
                        listaFiltrada.add(item)
                    }
                }
            }

            //Creamos el objeto de tipo FilterResults()
            val results = FilterResults()
            //Asignamos los resultados añadido al ArrayList a los valores de FilterResults
            results.values = listaFiltrada

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults)
        {
            items.clear()
            items.addAll(results.values as ArrayList<Item>)
            notifyDataSetChanged()
        }
    }

}

