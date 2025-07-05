package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.playlistmaker.R


class PlaybackButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isPlaying: Boolean = false
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var currentDrawable: Drawable? = null

    private val imageBounds = RectF()

    private var onToggleRequest: ((Boolean) -> Unit)? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PlaybackButtonView, 0, 0)
            playDrawable = typedArray.getDrawable(R.styleable.PlaybackButtonView_playIcon)
            pauseDrawable = typedArray.getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
            typedArray.recycle()
        }

        currentDrawable = playDrawable
        isClickable = true
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            currentDrawable = if (isPlaying) pauseDrawable else playDrawable
            invalidate()
        }
    }

    fun setOnPlaybackToggleListener(listener: (Boolean) -> Unit) {
        onToggleRequest = listener
    }

    fun clearListener() {
        onToggleRequest = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
            onToggleRequest?.invoke(!isPlaying)
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageBounds.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentDrawable?.let {
            it.setBounds(
                imageBounds.left.toInt(),
                imageBounds.top.toInt(),
                imageBounds.right.toInt(),
                imageBounds.bottom.toInt()
            )
            it.draw(canvas)
        }
    }

}