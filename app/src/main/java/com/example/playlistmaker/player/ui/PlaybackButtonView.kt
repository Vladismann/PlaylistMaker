package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.playlistmaker.R


class PlaybackButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isPlaying: Boolean = false
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    private var onToggleRequest: ((Boolean) -> Unit)? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PlaybackButtonView, 0, 0)
            playDrawable = typedArray.getDrawable(R.styleable.PlaybackButtonView_playIcon)
            pauseDrawable = typedArray.getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
            typedArray.recycle()
        }

        updateIcon()
        isClickable = true
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
        updateIcon()
    }

    fun setOnPlaybackToggleListener(listener: (Boolean) -> Unit) {
        onToggleRequest = listener
    }

    private fun updateIcon() {
        setImageDrawable(if (isPlaying) pauseDrawable else playDrawable)
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
}