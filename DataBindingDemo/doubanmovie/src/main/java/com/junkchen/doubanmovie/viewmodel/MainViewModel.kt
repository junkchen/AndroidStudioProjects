package com.junkchen.doubanmovie.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import android.view.View
import com.junkchen.doubanmovie.model.data.RetrofitHelper
import com.junkchen.doubanmovie.model.entity.Movie
import com.junkchen.doubanmovie.view.CompletedListener
import com.junkchen.doubanmovie.adapter.MovieAdapter
import rx.Subscriber

/**
 * Created by Junk Chen on 2018/12/17.
 */
class MainViewModel(val movieAdapter: MovieAdapter, val completedListener: CompletedListener) {
    private val TAG: String by lazy { MainViewModel::class.java.simpleName }

    val contentViewVisibility: ObservableField<Int> = ObservableField()
    val progressBarVisibility: ObservableField<Int> = ObservableField()
    val errorInfoLayoutVisibility: ObservableField<Int> = ObservableField()
    val exception: ObservableField<String> = ObservableField()
    private var subscriber: Subscriber<Movie>? = null

    init {
        initData()
        getMovies()
    }

    private fun getMovies() {
        subscriber = object : Subscriber<Movie>() {
            override fun onNext(movie: Movie?) {
                movie?.let { movieAdapter.addItem(it) }
            }

            override fun onCompleted() {
                hideAll()
                contentViewVisibility.set(View.VISIBLE)
                completedListener.onCompleted()
            }

            override fun onError(e: Throwable?) {
                Log.i(TAG, "onError: ${e?.message}")
                hideAll()
                errorInfoLayoutVisibility.set(View.VISIBLE)
                exception.set(e?.message)
                completedListener.onCompleted()
            }
        }
        subscriber?.let { RetrofitHelper.getInstance().getMovie(it, start = 0, count = 30) }
    }

    fun refreshData() {
        getMovies()
    }

    private fun initData() {
        contentViewVisibility.set(View.GONE)
        progressBarVisibility.set(View.GONE)
        errorInfoLayoutVisibility.set(View.GONE)
    }

    private fun hideAll() {
        contentViewVisibility.set(View.GONE)
        progressBarVisibility.set(View.GONE)
        errorInfoLayoutVisibility.set(View.GONE)
    }
}