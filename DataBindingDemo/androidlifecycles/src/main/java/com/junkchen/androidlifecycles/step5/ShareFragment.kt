package com.junkchen.androidlifecycles.step5


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.junkchen.androidlifecycles.R

/**
 * A simple [Fragment] subclass.
 *
 */
class ShareFragment : Fragment() {

    private lateinit var shareViewModel: ShareViewModel
    private lateinit var seekBar: SeekBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_share, container, false)

        // 使用 getActivity() 让该 activity 下的 fragment 接收到的都是同一个 ViewModel 实例
        shareViewModel = ViewModelProviders.of(this.activity!!).get(ShareViewModel::class.java)

        seekBar = root.findViewById(R.id.seekBar)

        subscribeSeekBar()
        return root
    }

    private fun subscribeSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                // Set the ViewModel's value when the change comes from the user.
                shareViewModel.seekBarValue.value = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // Update the SeekBar when the ViewModel is changed.
        shareViewModel.seekBarValue.observe(this, Observer { progress ->
            progress?.let {
                seekBar.progress = it
            }
        })
    }

}
