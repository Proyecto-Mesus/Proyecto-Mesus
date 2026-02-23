package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemEventBinding

class EventAdapter(
    private var fullList: List<Evento>,
    private val onItemClick: (Evento) -> Unit,
    private val onLongClick: (Evento, android.view.View) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(), android.widget.Filterable {

    private var filteredList: List<Evento> = fullList.toList()

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = filteredList[position]
        holder.binding.tvEventName.text = event.nombre
        holder.binding.tvEventDate.text = event.fecha
        holder.binding.tvEventDescription.text = event.descripcion
        
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
        
        holder.itemView.setOnLongClickListener {
            onLongClick(event, it)
            true
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Evento>) {
        fullList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    override fun getFilter(): android.widget.Filter {
        return object : android.widget.Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = if (query.isEmpty()) {
                    fullList
                } else {
                    fullList.filter {
                        it.nombre.lowercase().contains(query) ||
                        it.descripcion.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Evento> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
