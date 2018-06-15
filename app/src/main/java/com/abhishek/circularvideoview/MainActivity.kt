package com.abhishek.circularvideoview

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), VideoRendererEventListener {

    private val TAG = "MainActivity"
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var resolutionTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Create a default TrackSelector
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        // 2. Create a default LoadControl
        val loadControl = DefaultLoadControl()

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        playerView = PlayerView(this)
        playerView = exo_player

        //Set media controller
        playerView!!.useController = true
        playerView!!.requestFocus()

        // Bind the player to the view.
        playerView!!.player = player


        // I. ADJUST HERE:
        //CHOOSE CONTENT: LiveStream / SdCard

        //LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:

        //        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
        val mp4VideoUri =
            Uri.parse("http://54.255.155.24:1935//Live/_definst_/amlst:sweetbcha1novD235L240P/playlist.m3u8") //Radnom 540p indian channel
        //        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------


        //VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
        //        String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
        //        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);


        //Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeterA = DefaultBandwidthMeter()
        //Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "exoplayer2example"),
            bandwidthMeterA)
        //Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()


        // II. ADJUST HERE:

        //This is the MediaSource representing the media to be played:
        //FOR SD CARD SOURCE:
        //        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

        //FOR LIVESTREAM LINK:
        val videoSource = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null)
        val loopingSource = LoopingMediaSource(videoSource)

        // Prepare the player with the source.
        player!!.prepare(loopingSource)

        player!!.addListener(object : ExoPlayer.EventListener {
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
                player!!.stop()
                player!!.prepare(loopingSource)
                player!!.playWhenReady = true
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...")
            }
        })

        player!!.playWhenReady = true //run file/link when ready to play.
        player!!.setVideoDebugListener(this) //for listening to resolution change and  outputing the resolution
    }//End of onCreate

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
}