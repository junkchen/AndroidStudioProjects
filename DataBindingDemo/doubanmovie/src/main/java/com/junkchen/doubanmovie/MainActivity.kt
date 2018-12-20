package com.junkchen.doubanmovie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junkchen.doubanmovie.view.Movie2Fragment

/**
 * Created by Junk Chen on 2018/12/17.
 */
class MainActivity : AppCompatActivity() {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DataBindingUtil.setContentView<com.junkchen.doubanmovie.databinding.ActivityMainBinding>(this, R.layout.activity_main)
        supportFragmentManager.beginTransaction()
//                .add(R.id.fl_movie, MovieFragment.newInstance())
                .add(R.id.fl_movie, Movie2Fragment.newInstance())
                .commitNow()
    }
}
