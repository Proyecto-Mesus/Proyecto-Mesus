package com.example.tarearetrofit.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tarearetrofit.databinding.FilmLayoutBinding

class FilmAdapter (val context: Context, var items: List<Film>): RecyclerView.Adapter<FilmAdapter.FilmViewHolder>()
{
    private var mListener: OnItemClickListener? = null


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
    ): FilmViewHolder {


        val binding = FilmLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return FilmViewHolder(context, binding, mListener)
    }


    override fun onBindViewHolder(
        holder: FilmViewHolder,
        position: Int
    ) {
        holder.bind(position,items[position])
    }


    override fun getItemCount(): Int {
        return items.size
    }


    class FilmViewHolder(val context: Context, val binding: FilmLayoutBinding, listener: OnItemClickListener?): RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.root.setOnClickListener{
                listener?.onItemClick(absoluteAdapterPosition)
            }
        }


        fun bind(position: Int, film: Film)
        {
            binding.pelicula.text = "Episode ${film.episode_id}: ${film.title}"
        }
    }
}