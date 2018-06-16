package com.abhishek.circularvideoview

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), VideoRendererEventListener, Player.EventListener {

    private val TAG = "MainActivity"
    private var player: SimpleExoPlayer? = null
    private var disposable: Disposable? = null
    private var loopingSource: LoopingMediaSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        exo_player.useController = false
        exo_player_full_screen.useController = false
        playVideoInCircle()

        val mp4VideoUri =
            Uri.parse("http://54.255.155.24:1935//Live/_definst_/amlst:sweetbcha1novD235L240P/playlist.m3u8")
        val bandwidthMeterA = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "exoplayer2example"),
            bandwidthMeterA)

        val videoSource = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null)
        loopingSource = LoopingMediaSource(videoSource)
        player?.prepare(loopingSource)
        player?.addListener(this)
        player?.playWhenReady = true

        disposable = Flowable
            .interval(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { n ->
                if ((n.toInt() % 2) == 0) {
                    Log.e("OOOOO", "playing full screen")
                    playVideoInFullScreenMode()
                } else {
                    Log.e("OOOOO", "playing circular one")
                    playVideoInCircle()
                }
            }
            .subscribe()
    }

    private fun playVideoInCircle() {
        val params = cv.layoutParams
        params.height = 200 + Random().nextInt(10) * 100
        cv.layoutParams = params

        rfl.visibility = View.VISIBLE
        cv.visibility = View.VISIBLE
        exo_player_full_screen.visibility = View.GONE

        exo_player.player = player
        exo_player.requestFocus()
    }

    private fun playVideoInFullScreenMode() {
        rfl.visibility = View.GONE
        cv.visibility = View.GONE
        exo_player_full_screen.visibility = View.VISIBLE

        exo_player_full_screen.player = player
        exo_player_full_screen.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
        disposable?.dispose()
    }

    override fun onVideoEnabled(counters: DecoderCounters) {

    }

    override fun onVideoDecoderInitialized(decoderName: String,
                                           initializedTimestampMs: Long,
                                           initializationDurationMs: Long) {

    }

    override fun onVideoInputFormatChanged(format: Format) {

    }

    override fun onDroppedFrames(count: Int, elapsedMs: Long) {

    }

    override fun onVideoSizeChanged(width: Int,
                                    height: Int,
                                    unappliedRotationDegrees: Int,
                                    pixelWidthHeightRatio: Float) {
        Log.v(TAG, "onVideoSizeChanged [ width: $width height: $height]")
    }

    override fun onRenderedFirstFrame(surface: Surface) {

    }

    override fun onVideoDisabled(counters: DecoderCounters) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        Log.v(TAG, "Listener-onTimelineChanged...")
    }

    override fun onPositionDiscontinuity(reason: Int) {
        Log.v(TAG, "Listener-onPositionDiscontinuity...")
    }

    override fun onSeekProcessed() {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray,
                                 trackSelections: TrackSelectionArray) {
        Log.v(TAG, "Listener-onTracksChanged...")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.v(TAG, "Listener-onLoadingChanged...isLoading:$isLoading")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.v(TAG, "Listener-onPlayerStateChanged...$playbackState")
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.v(TAG, "Listener-onRepeatModeChanged...")
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.v(TAG, "Listener-onPlayerError...")
        player?.stop()
        player?.prepare(loopingSource)
        player?.playWhenReady = true
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        Log.v(TAG, "Listener-onPlaybackParametersChanged...")
    }
}