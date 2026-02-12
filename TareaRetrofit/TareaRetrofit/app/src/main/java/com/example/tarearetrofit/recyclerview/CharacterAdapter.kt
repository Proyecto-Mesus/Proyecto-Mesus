package com.example.tarearetrofit.recyclerview


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tarearetrofit.databinding.ItemLayoutBinding


class CharacterAdapter(val context: Context, var items: List<Character>): RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>()
{
    private lateinit var mListener: OnItemClickListener


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        mListener = listener
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterViewHolder {


        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CharacterViewHolder(context, binding, mListener)
    }


    override fun onBindViewHolder(
        holder: CharacterViewHolder,
        position: Int
    ) {
        holder.bind(position,items[position])
    }


    override fun getItemCount(): Int {
        return items.size
    }


    class CharacterViewHolder(val context: Context, val binding: ItemLayoutBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.root.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }


        fun bind(position: Int, character: Character)
        {
            binding.tvName.text = character.name
            binding.tvGender.text = character.gender
            binding.tvHeight.text = character.height
        }
    }
}
