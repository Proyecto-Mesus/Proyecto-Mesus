package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemConversacionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private var conversaciones: List<Conversacion>,
    private val onChatClick: (Conversacion) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(private val binding: ItemConversacionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(conversacion: Conversacion) {
            binding.tvUserName.text = conversacion.otroUsuarioNombre
            binding.tvLastMessage.text = conversacion.ultimoMensaje
            
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvTimestamp.text = sdf.format(Date(conversacion.timestamp))

            binding.root.setOnClickListener {
                onChatClick(conversacion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemConversacionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(conversaciones[position])
    }

    override fun getItemCount(): Int = conversaciones.size

    fun updateData(newData: List<Conversacion>) {
        conversaciones = newData
        notifyDataSetChanged()
    }
}
