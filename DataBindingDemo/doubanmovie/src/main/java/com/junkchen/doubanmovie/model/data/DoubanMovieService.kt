package com.junkchen.doubanmovie.model.data

import com.junkchen.doubanmovie.model.entity.Movie
import com.junkchen.doubanmovie.model.entity.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Junk Chen on 2018/12/17.
 */
interface DoubanMovieService {
    companion object {
        val BASE_URL = "https://api.douban.com/v2/movie/"
    }

    @GET("top250")
    fun getMovies(@Query("start") start: Int,
                  @Query("count") count: Int): Observable<Response<List<Movie>>>
}