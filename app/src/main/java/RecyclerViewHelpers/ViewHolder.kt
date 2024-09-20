package RecyclerViewHelpers

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ricardo.galvez.recuperacionandroid.R

class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val txtNombreCard = view.findViewById<TextView>(R.id.txtNombreCard)
    val imgEditar = view.findViewById<ImageView>(R.id.imgEditar)
    val imgEliminar = view.findViewById<ImageView>(R.id.imgEliminar)
}