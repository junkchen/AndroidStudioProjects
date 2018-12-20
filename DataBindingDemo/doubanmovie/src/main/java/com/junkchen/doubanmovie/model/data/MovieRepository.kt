package com.junkchen.doubanmovie.model.data

import android.util.Log
import com.junkchen.doubanmovie.model.entity.Movie
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MovieRepository private constructor() {
    private var retrofit: Retrofit
    private var builder: OkHttpClient.Builder
    private var movieService: DoubanMovieService

    init {
        builder = OkHttpClient.Builder().apply {
            connectTimeout(MovieRepository.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        }

        retrofit = Retrofit.Builder().apply {
            this.client(builder.build())
            this.addConverterFactory(GsonConverterFactory.create())
            this.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            this.baseUrl(DoubanMovieService.BASE_URL)
        }.build()

        movieService = retrofit.create(DoubanMovieService::class.java)
    }

    fun getMovies(start: Int = 0, count: Int = 10) {
        movieService.getMovies(start, count)
                .map {
                    Log.i(RetrofitHelper.TAG, "map: response: $it")
                    it.subjects
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Movie>?> {
                    override fun onError(e: Throwable?) {

                    }

                    override fun onNext(t: List<Movie>?) {

                    }

                    override fun onCompleted() {

                    }
                })
    }

    fun getMovies(subcriber: Subscriber<List<Movie>>, start: Int = 0, count: Int = 10) {
        movieService.getMovies(start, count)
                .map {
                    Log.i(RetrofitHelper.TAG, "map: response: $it")
                    it.subjects
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subcriber)
    }

    companion object {
        val TAG by lazy { MovieRepository::class.java.simpleName }
        private val DEFAULT_TIMEOUT = 10L

        // For Singleton instantiation
        @Volatile
        private var instance: MovieRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: MovieRepository().also { instance = it }
        }
    }
}