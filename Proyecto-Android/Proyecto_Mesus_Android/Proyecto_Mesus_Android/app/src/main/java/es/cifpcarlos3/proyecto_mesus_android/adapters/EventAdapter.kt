package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemEventBinding

class EventAdapter(private val events: List<Evento>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.binding.tvEventName.text = event.nombre
        holder.binding.tvEventDate.text = event.fecha
        holder.binding.tvEventDescription.text = event.descripcion
    }

    override fun getItemCount(): Int = events.size
}
