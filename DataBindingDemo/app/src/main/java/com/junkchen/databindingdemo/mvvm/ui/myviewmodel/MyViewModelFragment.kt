package com.junkchen.databindingdemo.mvvm.ui.myviewmodel

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junkchen.databindingdemo.R

class MyViewModelFragment : Fragment() {

    companion object {
        fun newInstance() = MyViewModelFragment()
    }

    private lateinit var viewModel: MyViewModelViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.my_view_model_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyViewModelViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel
    }

}
