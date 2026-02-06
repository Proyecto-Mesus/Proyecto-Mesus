package es.cifpcarlos3.proyecto_mesus_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.proyecto_mesus_android.data.models.MercadoItem
import es.cifpcarlos3.proyecto_mesus_android.databinding.ItemMarketplaceBinding

class MarketplaceAdapter(private val items: List<MercadoItem>) : RecyclerView.Adapter<MarketplaceAdapter.MarketplaceViewHolder>() {

    class MarketplaceViewHolder(val binding: ItemMarketplaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketplaceViewHolder {
        val binding = ItemMarketplaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarketplaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketplaceViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvItemName.text = item.nombre
        holder.binding.tvCardName.text = item.nombreCarta ?: ""
        holder.binding.tvItemPrice.text = "${item.precio}€"
    }

    override fun getItemCount(): Int = items.size
}
