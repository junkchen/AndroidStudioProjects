package com.junkchen.doubanmovie.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.junkchen.doubanmovie.R
import com.junkchen.doubanmovie.adapter.MovieAdapter
import com.junkchen.doubanmovie.databinding.FragmentMovieBinding
import com.junkchen.doubanmovie.viewmodel.MainViewModel

/**
 * Movie list fragment.
 * A simple [Fragment] subclass.
 * Use the [MovieFragment.newInstance] factory method to
 * create an instance of this fragment.
 * Created by Junk Chen on 2018/12/17.
 */
class MovieFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CompletedListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var fragmentMovieBinding: FragmentMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_movie, container, false)
        fragmentMovieBinding = FragmentMovieBinding.bind(contentView)
        initData()
        return contentView
    }

    private fun initData() {
        movieAdapter = MovieAdapter()
        fragmentMovieBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        fragmentMovieBinding.recyclerView.itemAnimator = DefaultItemAnimator()
        fragmentMovieBinding.recyclerView.adapter = movieAdapter
        fragmentMovieBinding.swipeRefreshLayout.setOnRefreshListener(this)
        viewModel = MainViewModel(movieAdapter, this)
        fragmentMovieBinding.viewModel = viewModel
    }

    override fun onRefresh() {
        movieAdapter.clearItems()
        viewModel.refreshData()
    }

    override fun onCompleted() {
        if (fragmentMovieBinding.swipeRefreshLayout.isRefreshing) {
            fragmentMovieBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MovieFragment.
         */
        @JvmStatic
        fun newInstance() = MovieFragment()
    }
}
