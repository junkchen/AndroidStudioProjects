package com.junkchen.doubanmovie.viewmodel

import android.view.View
import android.widget.ImageView
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.junkchen.doubanmovie.model.entity.Movie

/**
 * Created by Junk Chen on 2018/12/17.
 */
class MovieViewModel(private val movie: Movie) : BaseObservable() {
    fun getCoverUrl() = movie.images.small
    fun getTitle() = movie.title
    fun getRating() = movie.rating.average
    fun getRatingText() = movie.rating.average.toString()
    fun getYear() = movie.year
    fun getMovieType() = with(StringBuilder()) {
        movie.genres.forEach {
            append("$it ")
        }
        toString()
    }

    fun getImageUrl() = movie.images.small

    fun doClick(view: View) {
        Snackbar.make(view,
                "Name: ${movie.title}, Rating: ${movie.rating.average}, Year: ${movie.year}",
                Snackbar.LENGTH_SHORT).show()
    }

//    companion object {
//        @BindingAdapter(value = ["image_Url"], requireAll = true)
//        @JvmStatic
//        fun setImageUrl(imageView: ImageView, url: String?) {
//            Glide.with(imageView.context)
//                    .load(url)
//                    .placeholder(R.drawable.cover)
//                    .error(R.drawable.cover)
//                    .into(imageView)
//        }
//    }
}

@BindingAdapter("app:imgUrl")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
            .load(url)
//            .placeholder(R.drawable.cover)
//            .error(R.drawable.cover)
            .into(imageView)
}
