package com.junkchen.doubanmovie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.junkchen.doubanmovie.R
import com.junkchen.doubanmovie.databinding.ItemMoviesBinding
import com.junkchen.doubanmovie.model.entity.Movie
import com.junkchen.doubanmovie.viewmodel.MovieViewModel

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.BindingHolder>() {

    private val movies: MutableList<Movie>

    init {
        movies = arrayListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val movieBinding: ItemMoviesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_movies, parent, false)
        return BindingHolder(movieBinding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: BindingHolder, postion: Int) {
        val movieViewModel = MovieViewModel(movies[postion])
        holder.itemMovie.viewModel = movieViewModel
    }

    fun addItem(movie: Movie) {
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }

    fun addMovies(movies: List<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun clearItems() {
        movies.clear()
        notifyDataSetChanged()
    }

    class BindingHolder(val itemMovie: ItemMoviesBinding) : RecyclerView.ViewHolder(itemMovie.cardView)
}