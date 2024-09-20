package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.tbDoctor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ricardo.galvez.recuperacionandroid.R


class Adaptador(var Datos: List<tbDoctor>): RecyclerView.Adapter<ViewHolder>() {

    fun actualizarPantalla(UUID_Doctor2: String, NuevoDoctor: String){
        val index = Datos.indexOfFirst { it.UUID_Doctor == UUID_Doctor2 }
        Datos[index].Nombre_Doctor = NuevoDoctor
        notifyDataSetChanged()
    }
    fun eliminarRegistro(Nombre_Doctor: String, position: Int){
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteDoctor = objConexion?.prepareStatement("delete tbDoctor where Nombre_Doctor = ?")!!
            deleteDoctor.setString(1, Nombre_Doctor)
            deleteDoctor.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun actualizarRegistro(Nombre_Doctor: String, UUID_Doctor: String){
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val updateDoctor = objConexion?.prepareStatement("update tbDoctor set Nombre_Doctor = ? where UUID_Doctor = ?")!!
            updateDoctor.setString(1, Nombre_Doctor)
            updateDoctor.setString(2, UUID_Doctor)
            updateDoctor.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
            withContext(Dispatchers.Main){
                actualizarPantalla(UUID_Doctor, Nombre_Doctor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Datos[position]
        holder.txtNombreCard.text = item.Nombre_Doctor

        holder.imgEliminar.setOnClickListener {
            val contexto = holder.itemView.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("Estas seguro que deseas eliminar?")

            builder.setPositiveButton("Si"){
                dialog, wich ->
                eliminarRegistro(item.Nombre_Doctor, position)
            }

            builder.setNegativeButton("No"){
                dialog, wich ->
                dialog.dismiss()
            }

            builder.show()
        }

        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("Desea actualizar el doctor?")

            val cuadroTexto = EditText(context)

            cuadroTexto.setHint(item.Nombre_Doctor)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar"){ dialog, wich ->
                actualizarRegistro(cuadroTexto.text.toString(), item.UUID_Doctor)
            }

            builder.setNegativeButton("Cancelar"){ dialog, wich ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}
