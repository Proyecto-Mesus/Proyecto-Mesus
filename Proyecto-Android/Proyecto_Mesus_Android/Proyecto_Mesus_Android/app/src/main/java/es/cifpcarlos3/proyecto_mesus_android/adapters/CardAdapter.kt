package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemCardBinding

import android.widget.Filter
import android.widget.Filterable
import androidx.viewbinding.ViewBinding
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemCardGridBinding

class CardAdapter(private var fullList: List<Carta>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>(), Filterable {

    private var filteredList: List<Carta> = fullList.toList()
    private var isGridView: Boolean = false

    companion object {
        private const val VIEW_TYPE_LIST = 0
        private const val VIEW_TYPE_GRID = 1
    }

    class CardViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == VIEW_TYPE_GRID) {
            ItemCardGridBinding.inflate(inflater, parent, false)
        } else {
            ItemCardBinding.inflate(inflater, parent, false)
        }
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = filteredList[position]

        if (holder.binding is ItemCardBinding) {
            holder.binding.tvCardName.text = card.nombre
            holder.binding.tvCardDescription.text = "${card.set} #${card.numeroSet}"
            loadCardImage(holder.binding.ivCardImage, card.imagen)
        } else if (holder.binding is ItemCardGridBinding) {
            holder.binding.tvCardName.text = card.nombre
            holder.binding.tvCardDescription.text = "${card.set} #${card.numeroSet}"
            loadCardImage(holder.binding.ivCardImage, card.imagen)
        }
    }

    private fun loadCardImage(imageView: android.widget.ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imageView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Carta>) {
        fullList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    fun setViewType(isGrid: Boolean) {
        isGridView = isGrid
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
                        it.nombre.lowercase().contains(query) ||
                        it.set.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Carta> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
