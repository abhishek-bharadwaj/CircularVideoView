package com.abhishek.circularvideoview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout

class RoundFrameLayout @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val clip = Path()

    private var posX: Int = 0
    private var posY: Int = 0
    private var radius: Int = 0

    init {
        // We can use outlines on 21 and up for anti-aliased clipping.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = true
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        posX = Math.round(width.toFloat() / 2)
        posY = Math.round(height.toFloat() / 2)


        radius = Math.floor((Math.min(width, height).toFloat() / 2).toDouble()).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = OutlineProvider(posX, posY, radius)
        } else {
            clip.reset()
            clip.addCircle(posX.toFloat(), posY.toFloat(), radius.toFloat(), Path.Direction.CW)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        // Not needed on 21 and up since we're clipping to the outline instead.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            canvas.clipPath(clip)
        }

        super.dispatchDraw(canvas)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Don't pass touch events that occur outside of our clip to the children.
        val distanceX = Math.abs(event.x - posX)
        val distanceY = Math.abs(event.y - posY)
        val distance = Math.hypot(distanceX.toDouble(), distanceY.toDouble())

        return distance > radius
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    internal class OutlineProvider(posX: Int, posY: Int, radius: Int) : ViewOutlineProvider() {

        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        init {
            left = posX - radius
            top = posY - radius
            right = posX + radius
            bottom = posY + radius
        }

        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(left, top, right, bottom)
        }
    }
}
