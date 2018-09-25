package com.junkchen.playsound

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import java.io.File

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MediaPlayerUtils.getInstance(this)
//        initMediaPlayer()
//        playSoundByMediaPlayer()
//        playSound()
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying) mMediaPlayer.stop()
            mMediaPlayer.release()
        }
        MediaPlayerUtils.getInstance(this).destroy()
        super.onDestroy()
    }

    fun speech(v: View) {
//        playSoundByMediaPlayer()
        MediaPlayerUtils.getInstance(this).play("5b234d3b-2e2e-438a-b4a4-660e2a8a2927.mp3")
    }

    lateinit var mMediaPlayer: MediaPlayer
    fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer()
    }

    fun playSoundByMediaPlayer() {
//        val mediaPlayer = MediaPlayer.create(this, R.raw.welcome)
//        mediaPlayer.start()

        mMediaPlayer.reset()
        val path = Environment.getExternalStorageDirectory().path + File.separator + "/5b234d3b-2e2e-438a-b4a4-660e2a8a2927.mp3"
//        val path = Environment.getExternalStorageDirectory().path + "/welcome.ogg"
        val fd = assets.openFd("5b234d3b-2e2e-438a-b4a4-660e2a8a2927.mp3")
//        Thread({
        Log.i(TAG, "---run start---: $path")
//            mMediaPlayer.setDataSource(path)
        mMediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mMediaPlayer.prepareAsync()
//            mMediaPlayer.setOnCompletionListener {_ -> Log.i(TAG, "123456mediaplayer") }
        mMediaPlayer.setOnPreparedListener {
            //                it ->
//                Log.i(TAG, "prepare finish")
//                it.start()
            mMediaPlayer.start()
        }
//        }).start()

    }

    fun playSound() {
        val mMaxStreams = 1
        val mAttrBuilder = AudioAttributes.Builder()
        mAttrBuilder.setUsage(AudioAttributes.USAGE_MEDIA)
        mAttrBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        val mAudioAttributes: AudioAttributes = mAttrBuilder.build()

        val mSoundPoolBuilder: SoundPool.Builder = SoundPool.Builder()
        mSoundPoolBuilder.setMaxStreams(mMaxStreams)
        mSoundPoolBuilder.setAudioAttributes(mAudioAttributes)

//        val mSoundPool: SoundPool = SoundPool.Builder().build()
        val mSoundPool: SoundPool = mSoundPoolBuilder.build()

        //5b234d3b-2e2e-438a-b4a4-660e2a8a2927.mp3
        val path = Environment.getExternalStorageDirectory().path + File.separator + "welcome.ogg"
        Log.i(TAG, "path: $path")
//        val load = mSoundPool.load(path, 1)
        val load = mSoundPool.load(this, R.raw.welcome, 1)
        Log.i(TAG, "load: $load")
//        mSoundPool.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status -> if (status == 0) mSoundPool.play(load, 1f, 1f, 1, -1, 1f) })
        mSoundPool.setOnLoadCompleteListener({ soundPool, sampleId, status ->
            if (status == 0) {
                val play = mSoundPool.play(load, 1f, 1f, 1, -1, 1f)
                Log.i(TAG, "play: " + if (play != 0) "($play)successful" else "($play)failed")
            }
        })
        /**
         * @param soundID a soundID returned by the load() function
         * @param leftVolume left volume value (range = 0.0 to 1.0)
         * @param rightVolume right volume value (range = 0.0 to 1.0)
         * @param priority stream priority (0 = lowest priority)
         * @param loop loop mode (0 = no loop, -1 = loop forever)
         * @param rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
         * @return non-zero streamID if successful, zero if failed
         */
//        val play = mSoundPool.play(load, 1f, 1f, 1, -1, 1f)
//        Log.i(TAG, "play: " + if (play != 0) "($play)successful" else "($play)failed")
    }
}
