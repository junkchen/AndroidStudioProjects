package com.junkchen.doubanmovie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.junkchen.doubanmovie.databinding.ItemMoviesBinding
import com.junkchen.doubanmovie.model.entity.Movie
import com.junkchen.doubanmovie.viewmodel.MovieViewModel

class Movie2Adapter : ListAdapter<Movie, Movie2Adapter.ViewHolder>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMoviesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position)
        holder.apply {
            bind(MovieViewModel(movie))
            itemView.tag = movie
        }
    }

    class ViewHolder(private val binding: ItemMoviesBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(movieModel: MovieViewModel) {
            binding.apply {
                this.viewModel = movieModel
            }
        }
    }
}