package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemCollectionBinding
import es.cifpcarlos3.proyecto_mesus_android.R

import android.widget.Filter
import android.widget.Filterable

class CollectionAdapter(
    private var fullList: List<Coleccion>,
    private val onItemClick: (Coleccion) -> Unit,
    private val onLongClick: (Coleccion, android.view.View) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>(), Filterable {

    private var filteredList: List<Coleccion> = fullList.toList()

    class CollectionViewHolder(val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = filteredList[position]
        holder.binding.tvCollectionName.text = collection.nombre
        
        val context = holder.itemView.context
        val gameName = when(collection.idJuego) {
            1 -> context.getString(R.string.gamePokemon)
            2 -> context.getString(R.string.gameMTG)
            3 -> context.getString(R.string.gameYugioh)
            else -> context.getString(R.string.gameOtro)
        }
        val visibility = if (collection.publica == 1) context.getString(R.string.publica) else context.getString(R.string.privada)
        holder.binding.tvCollectionDetails.text = "$gameName • $visibility"

        holder.itemView.setOnClickListener { onItemClick(collection) }
        holder.itemView.setOnLongClickListener { 
            onLongClick(collection, it)
            true
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Coleccion>) {
        this.fullList = newList
        this.filteredList = newList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = if (query.isEmpty()) {
                    fullList
                } else {
                    fullList.filter {
                        it.nombre.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Coleccion> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
