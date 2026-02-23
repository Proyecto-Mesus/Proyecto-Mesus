package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import es.cifpcarlos3.proyecto_mesus_android.data.models.Mensaje
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemMensajeEmisorBinding
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemMensajeReceptorBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MensajeAdapter(
    private var mensajes: List<Mensaje>,
    private val currentUserId: Int
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    companion object {
        private const val TYPE_EMISOR = 1
        private const val TYPE_RECEPTOR = 2
    }

    abstract class MensajeViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(mensaje: Mensaje)
    }

    inner class EmisorViewHolder(private val binding: ItemMensajeEmisorBinding) : MensajeViewHolder(binding) {
        override fun bind(mensaje: Mensaje) {
            binding.tvMessageContent.text = mensaje.contenido
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvMessageTime.text = sdf.format(Date(mensaje.timestamp))
        }
    }

    inner class ReceptorViewHolder(private val binding: ItemMensajeReceptorBinding) : MensajeViewHolder(binding) {
        override fun bind(mensaje: Mensaje) {
            binding.tvSenderName.text = mensaje.emisorNombre
            binding.tvMessageContent.text = mensaje.contenido
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvMessageTime.text = sdf.format(Date(mensaje.timestamp))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensajes[position].emisorId == currentUserId) TYPE_EMISOR else TYPE_RECEPTOR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        return if (viewType == TYPE_EMISOR) {
            val binding = ItemMensajeEmisorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmisorViewHolder(binding)
        } else {
            val binding = ItemMensajeReceptorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceptorViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        holder.bind(mensajes[position])
    }

    override fun getItemCount(): Int = mensajes.size

    fun updateData(newData: List<Mensaje>) {
        mensajes = newData
        notifyDataSetChanged()
    }
}
