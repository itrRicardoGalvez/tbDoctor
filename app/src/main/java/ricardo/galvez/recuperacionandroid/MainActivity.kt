package ricardo.galvez.recuperacionandroid

import Modelo.ClaseConexion
import Modelo.tbDoctor
import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtEdad = findViewById<TextView>(R.id.txtEdad)
        val txtPeso = findViewById<TextView>(R.id.txtPeso)
        val txtCorreo = findViewById<TextView>(R.id.txtCorreo)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val rcvDoctor = findViewById<RecyclerView>(R.id.rcvDoctor)

        rcvDoctor.layoutManager = LinearLayoutManager(this)

        fun obtenerDoctor(): List<tbDoctor>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbDoctor")!!

            val listaDoctor = mutableListOf<tbDoctor>()

            while (resultSet.next()){
                val UUID_Doctor = resultSet.getString("UUID_Doctor")
                val Nombre_Doctor = resultSet.getString("Nombre_Doctor")
                val Edad_Doctor = resultSet.getInt("Edad_Doctor")
                val Peso_Doctor = resultSet.getInt("Peso_Doctor")
                val Correo_Doctor = resultSet.getString("Correo_Doctor")

                val valoresJuntos = tbDoctor(UUID_Doctor, Nombre_Doctor, Edad_Doctor, Peso_Doctor, Correo_Doctor)

                listaDoctor.add(valoresJuntos)
            }
            return listaDoctor
        }

        CoroutineScope(Dispatchers.IO).launch {
            val doctorBD = obtenerDoctor()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(doctorBD)
                rcvDoctor.adapter = adapter
            }
        }

        btnIngresar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                val addDoctor = objConexion?.prepareStatement("insert into tbDoctor (UUID_Doctor, Nombre_Doctor, Edad_Doctor, Peso_Doctor, Correo_Doctor) values(?, ?, ?, ?, ?)")!!
                addDoctor.setString(1, UUID.randomUUID().toString())
                addDoctor.setString(2, txtNombre.text.toString())
                addDoctor.setInt(3, txtEdad.text.toString().toInt())
                addDoctor.setInt(4, txtPeso.text.toString().toInt())
                addDoctor.setString(5, txtCorreo.text.toString())
                addDoctor.executeUpdate()
            }
        }
    }
}