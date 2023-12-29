package com.example.prueba4


import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
// import com.example.recyclerview_kotlin.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.OutputStream

class AdminActivity : AppCompatActivity() {

    //Código de permiso OK
    val READ_EXTERNAL_PERMISSION_CODE = 1000;
    val CAMERA_PERMISSION_CODE =777


    private lateinit var auth: FirebaseAuth
    private lateinit var imagenTxt: String
    private lateinit var actividadTxt: String
    private lateinit var downloadUri: Uri

    private lateinit var img: ImageView
    private lateinit var uri: Uri


    //Launcher para acceder a la galería
    private val openGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                //Asignamos la imagen seleccionada al ImageView del Activity
                img.setImageURI(it.data?.data)
                //Guardamos la URI de la imagen, para poder subirla luego al Bucket
                uri = it.data?.data!!
            }
        }

    public override fun onStart()
    {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        Toast.makeText(applicationContext, "Usuario: " + currentUser.toString(),
            Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Initialize Firebase Auth
        auth = Firebase.auth

        Toast.makeText(applicationContext, auth.toString(),
            Toast.LENGTH_SHORT).show()

        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
       val botonCerrar : Button = findViewById(R.id.button3)
        botonCerrar.setOnClickListener{

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
        }

        //ImageView donde se muestra la imagen seleccionada
        img = findViewById(R.id.iv_imagen)

        /** Imagen de la cámara **/
        //ImageButton para seleccionar la imagen de la memoria interna
        val btnCamara: ImageButton = findViewById(R.id.ib_seleccionar_foto)

        btnCamara.setOnClickListener{
           
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
            //si el permiso no ha sido aceptado por el momento (por primera vez)
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                {
                    //estos permisos estan rechazados, si queires aceptar la camara tienes que ir a ajustes y hacerlo
                    //manualmente
                    Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_LONG).show()

                }else{
                    //perdir los persmisos
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE )

                }
            }else{
                //abrir la camara
                abrirCamara()
            }
            
        }


        /** Imagen desde la galería **/
        //ImageButton para seleccionar la imagen de la memoria interna
        val btnSeleccionar: ImageButton = findViewById(R.id.ib_seleccionar_imag)

        //Botón para seleccionar la imagen
        btnSeleccionar.setOnClickListener {

            // Check permission
            checkGaleryPermission()

        }



        //Button para subir una imagen al bucket de Firebase
        val btnSubir: Button = findViewById(R.id.btn_subir)

        /** Leemos el nombre de la imagen **/
        var imagen: TextInputLayout = findViewById(R.id.outlinedTF_Nombre_Imagen)
        val inputText = imagen.editText?.text.toString()

        imagen.editText?.doOnTextChanged()
        { inputText, _, _, _ ->
            // Respond to input text change
            imagenTxt = inputText.toString();
        }

        btnSubir.setOnClickListener{

            Toast.makeText(applicationContext,imagenTxt,
                Toast.LENGTH_SHORT ).show()

            /** COMPROBAR QUE URI Y TEXTO NO SON NULOS **/

            if(uri!=null && imagenTxt !=null){

           //Creamos la referencia para subir la imagen. Debe incluir el nombre de la imagen
            val folder: StorageReference = FirebaseStorage.getInstance().getReference(imagenTxt)
            //Lanzamos un task para subir la imagen a la nube de Firebase
            val uploadTask = folder.putFile(uri)

            //Llegados a este punto, tenemos la imagen subida
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }

                //Estamos solicitando la URL de la imagen para usarla posteriormente en Glide
                folder.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    downloadUri = task.result!!

                } else {
                    // Handle failures

                }
            }

            }else{

            }

        }

        val btnSubirActividad: Button = findViewById(R.id.btn_subir_actividad)

        /** Leemos el nombre de la imagen **/
        var actividad: TextInputLayout = findViewById(R.id.outlinedTF_Nombre_Actividad)
        val inputTextActividad = imagen.editText?.text.toString()

        actividad.editText?.doOnTextChanged()
        { inputText, _, _, _ ->
            // Respond to input text change
            actividadTxt = inputText.toString();
        }

        //Para subir la actividad a Firebase
        btnSubirActividad.setOnClickListener{

            //Obtenemos la referencia a la BBDD
            val db = Firebase.firestore

            //Obtenemos acceso a la colección
            val reservas = db.collection("actividades")

            //Este objeto será la información almacenada en el nuevo documento
            val act = hashMapOf(
                "id" to actividadTxt,
                "titulo" to actividadTxt,
                "descripcion" to "",
                "src" to downloadUri.toString()
            )

            //Creamos el nuevo documento
            reservas.document(actividadTxt)
                .set(act)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        }

    }//OnCreate
//-----------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------

    private lateinit var  file : File
    private fun createPhotoFile()  {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}",".jpg",dir)

    }

    private fun abrirCamara() {
        Toast.makeText(this, "Abriendo camara", Toast.LENGTH_SHORT).show()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            it.resolveActivity(packageManager).also { component ->
                createPhotoFile()
                //val photoFile = createPhotoFile()
                val photoUri : Uri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+".fileprovider",file)
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                uri=photoUri
            }
        }
        openCamera.launch(intent)
       // openCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    val openCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK){

            //--------------guardar en la galeria-----------

            guardarGaleria()
            ///-------------------------------------------


           //val data = result.data!!
           //val bitmap =data.extras!!.get("data") as Bitmap
             val bitmap = getBitmap()
             val smallBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false)
             img.setImageBitmap(smallBitmap)


        }
    }


    private fun resizeImage(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    private fun  guardarGaleria(){
        //crear un contenedor
        val content =createContent()
        //guardar imagen
        val uris = save(content)
        //limpiar el contenedor
        clearContents(content,uris)
    }

    private fun createContent() : ContentValues{
        val fileName = file.name
        val fileType ="image/jpg"
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE,fileType)
            put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
            put(MediaStore.MediaColumns.IS_PENDING,1)
        }
    }

    private fun save(content : ContentValues): Uri{
     var outputStream: OutputStream?
     var urim : Uri?
     application.contentResolver.also { resolver ->
         urim= resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content)
         outputStream = resolver.openOutputStream(urim!!)
     }

        outputStream.use { output ->
            getBitmap().compress(Bitmap.CompressFormat.JPEG,100,output)
        }
        return urim!!
    }
    private fun clearContents(content: ContentValues, uri :Uri){
        content.clear()
        content.put(MediaStore.MediaColumns.IS_PENDING,0)
        contentResolver.update(uri,content,null,null)
    }

    private fun getBitmap():Bitmap{
        return BitmapFactory.decodeFile(file.toString())
    }

//-----------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
    //Comprueba si el permiso ya ha sido aceptado
    private fun checkGaleryPermission()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está aceptado.
            requestGaleryPermission()

        }
        else
        {
            //El permiso está aceptado.
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            openGallery.launch(intent)

        }
    }

    //Solicita el permiso
    private fun requestGaleryPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            Toast.makeText(applicationContext, "Debes activar el permiso desde Ajustes",
                Toast.LENGTH_LONG).show()
        }
        else
        {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_PERMISSION_CODE)
        }

    }

    //Encargado de «escuchar» la respuesta que da el usuario al aceptar o rechazar el permiso.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_EXTERNAL_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón,
                    // podemos lanzar la funcionalidad desde aquí.
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setType("image/*")
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    openGallery.launch(intent)


                }
                else
                {
                    //El usuario ha rechazado el permiso,
                    // podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                    Toast.makeText(applicationContext, "No es posible leer de la galería sin permiso",
                        Toast.LENGTH_LONG).show()

                }
                return
            } CAMERA_PERMISSION_CODE ->{

            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                abrirCamara()
            }
            else
            {
                Toast.makeText(this, "Permisos rechazados camara por primera vez", Toast.LENGTH_LONG).show()
            //el persmisno no ha sido aceptado
            }
            }
            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }



}