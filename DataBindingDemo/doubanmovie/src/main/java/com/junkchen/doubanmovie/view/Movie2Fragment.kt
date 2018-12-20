package com.junkchen.doubanmovie.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.junkchen.doubanmovie.adapter.Movie2Adapter
import com.junkchen.doubanmovie.adapter.MovieAdapter
import com.junkchen.doubanmovie.databinding.FragmentMovie2Binding
import com.junkchen.doubanmovie.viewmodel.Main2ViewModel

/**
 * Movie list fragment.
 * A simple [Fragment] subclass.
 * Use the [Movie2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 * Created by Junk Chen on 2018/12/17.
 */
class Movie2Fragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CompletedListener {
    private lateinit var viewModel: Main2ViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movie2Adapter: Movie2Adapter
    private lateinit var fragmentMovie2Binding: FragmentMovie2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(Main2ViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMovie2Binding.inflate(inflater, container, false)
        fragmentMovie2Binding = binding
        initData()
        return binding.root
    }

    private fun initData() {
        movieAdapter = MovieAdapter()
        movie2Adapter = Movie2Adapter()
        fragmentMovie2Binding.recyclerView.layoutManager = LinearLayoutManager(context)
        fragmentMovie2Binding.recyclerView.itemAnimator = DefaultItemAnimator()
        fragmentMovie2Binding.recyclerView.adapter = movie2Adapter
        fragmentMovie2Binding.swipeRefreshLayout.setOnRefreshListener(this)
        fragmentMovie2Binding.viewModel = viewModel.apply {
//            movieAdapter = this@Movie2Fragment.movie2Adapter
            completedListener = this@Movie2Fragment
        }

        viewModel.movies.observe(viewLifecycleOwner, Observer { movies ->
            if (movies.isNotEmpty()) {
//                movieAdapter.addMovies(movies)
                movie2Adapter.submitList(movies)
            }
        })
    }

    override fun onRefresh() {
        movieAdapter.clearItems()
        viewModel.refreshData()
    }

    override fun onCompleted() {
        if (fragmentMovie2Binding.swipeRefreshLayout.isRefreshing) {
            fragmentMovie2Binding.swipeRefreshLayout.isRefreshing = false
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
        fun newInstance() = Movie2Fragment()
    }
}
