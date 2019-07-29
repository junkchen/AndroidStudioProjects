package com.junkchen.cameraview


import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_texture_play_video.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [TexturePlayVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TexturePlayVideoFragment : Fragment() {

    private var mMediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_texture_play_video, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        aftv_video.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                this@TexturePlayVideoFragment.surface = null
                this@TexturePlayVideoFragment.mMediaPlayer?.stop()
                this@TexturePlayVideoFragment.mMediaPlayer?.release()
                return true
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                this@TexturePlayVideoFragment.surface = Surface(surface)
                PlayerVideo().start()
            }
        }
    }

    override fun onDestroy() {
        surface = null
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        super.onDestroy()
    }

    private inner class PlayerVideo : Thread() {
        override fun run() {
            try {
                val file = File("${context?.getExternalFilesDir(null)?.absolutePath}/1562809740474.mp4")
                mMediaPlayer = MediaPlayer().apply {
                    this.setDataSource(file.absolutePath)
                    this.setSurface(surface)
                    this.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    this.setOnPreparedListener { mMediaPlayer?.start() }
                    this.isLooping = true
                    this.prepare()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TexturePlayVideoFragment.
         */
        @JvmStatic
        fun newInstance() = TexturePlayVideoFragment()
    }
}
