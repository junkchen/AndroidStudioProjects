package com.junkchen.doubanmovie.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.junkchen.doubanmovie.adapter.Movie2Adapter
import com.junkchen.doubanmovie.model.data.MovieRepository
import com.junkchen.doubanmovie.model.entity.Movie
import com.junkchen.doubanmovie.view.CompletedListener
import rx.Subscriber

/**
 * Created by Junk Chen on 2018/12/17.
 */
class Main2ViewModel : ViewModel() {
    private val TAG: String by lazy { Main2ViewModel::class.java.simpleName }

    val movieRepository = MovieRepository.getInstance()
//    lateinit var movie2Adapter: Movie2Adapter
    lateinit var completedListener: CompletedListener
    val contentViewVisibility: ObservableField<Int> = ObservableField()
    val progressBarVisibility: ObservableField<Int> = ObservableField()
    val errorInfoLayoutVisibility: ObservableField<Int> = ObservableField()
    val exception: ObservableField<String> = ObservableField()
    private var subscriber: Subscriber<List<Movie>>? = null

    val movies = MutableLiveData<List<Movie>>()

    init {
        initData()
        getMovies()
    }

    private fun getMovies(start: Int = 0, count: Int = 10) {
        subscriber = object : Subscriber<List<Movie>>() {
            override fun onNext(t: List<Movie>?) {
                t?.let { movies.value = it }
            }

            override fun onCompleted() {
                hideAll()
                contentViewVisibility.set(View.VISIBLE)
                completedListener.onCompleted()
            }

            override fun onError(e: Throwable?) {
                hideAll()
                errorInfoLayoutVisibility.set(View.VISIBLE)
                exception.set(e?.message)
                completedListener.onCompleted()
            }
        }

        subscriber?.let { movieRepository.getMovies(it, start, count) }
    }

    fun refreshData() {
        getMovies(2, 6)
    }

    private fun initData() {
        contentViewVisibility.set(View.GONE)
        progressBarVisibility.set(View.VISIBLE)
        errorInfoLayoutVisibility.set(View.GONE)
    }

    private fun hideAll() {
        contentViewVisibility.set(View.GONE)
        progressBarVisibility.set(View.GONE)
        errorInfoLayoutVisibility.set(View.GONE)
    }
}