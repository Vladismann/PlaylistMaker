package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.DataConst.SEARCH_PREFS
import com.example.playlistmaker.DataConst.TRACK_TO_AUDIO_PLAYER_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
        private const val INPUT_KEY = "ACTUAL_TEXT"
        private const val TRACK_HISTORY_KEY = "tracks"
        private const val TRACK_HISTORY_SIZE = 10
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var sharedPreferences: SharedPreferences

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ItunesApi by lazy {
        retrofit.create(ItunesApi::class.java)
    }

    private var actualInput: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var tracks: List<Track>
    private lateinit var errorElement: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var rvTrack: RecyclerView
    private var handler: Handler? = null
    private var isClickAllowed = true
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        sharedPreferences = getSharedPreferences(SEARCH_PREFS, MODE_PRIVATE)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.seToolbar)
        setSupportActionBar(toolbar)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val searchHistory = findViewById<LinearLayout>(R.id.historyElement)
        progressBar = findViewById(R.id.searchProgressBar)

        inputEditText = findViewById(R.id.input_search)
        errorElement = findViewById(R.id.errorMessageElement)
        refreshButton = findViewById(R.id.refreshTracks)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        tracks = arrayListOf()
        trackAdapter = TrackAdapter(tracks)
        val tracksSearchHistory = readTracksFromPrefs()
        val trackHistoryAdapter = TrackAdapter(tracksSearchHistory)
        val clearHistoryButton: Button = findViewById(R.id.clearSearchHistory)

        rvTrack = findViewById(R.id.rvTrack)
        rvTrack.adapter = trackAdapter
        val rvTrackHistory: RecyclerView = findViewById(R.id.rvTrackHistory)
        rvTrackHistory.adapter = trackHistoryAdapter

        trackAdapter.setOnItemClickListener { position ->
            if (clickDebounce()) {
                val clickedTrack = tracks[position]
                val listToEdit = trackHistoryAdapter.getItems().toMutableList()

                if (listToEdit.contains(clickedTrack)) {
                    listToEdit.remove(clickedTrack)
                }
                if (listToEdit.size == TRACK_HISTORY_SIZE) {
                    listToEdit.removeAt(TRACK_HISTORY_SIZE - 1)
                }
                listToEdit.add(0, clickedTrack)
                writeTracksToPrefs(listToEdit.toTypedArray())
                writeTrackToPrefs(clickedTrack)
                trackHistoryAdapter.updateData(listToEdit.toList())

                val displayIntent = Intent(this, AudioPlayerActivity::class.java)
                startActivity(displayIntent)
            }
        }

        trackHistoryAdapter.setOnItemClickListener { position ->
            if (clickDebounce()) {
                val clickedTrack = trackHistoryAdapter.getItems()[position]
                writeTrackToPrefs(clickedTrack)
                val displayIntent = Intent(this, AudioPlayerActivity::class.java)
                startActivity(displayIntent)
            }
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackAdapter.clear()
            rvTrack.visibility = View.GONE
            errorElement.visibility = View.GONE
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                actualInput = s.toString()
                searchHistory.visibility = View.GONE
                if (inputEditText.text.isEmpty() && trackHistoryAdapter.itemCount != 0) {
                    progressBar.visibility = View.VISIBLE
                    searchHistory.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    trackAdapter.clear()
                }
                if (inputEditText.text.isEmpty() && trackHistoryAdapter.itemCount == 0) {
                    trackAdapter.clear()
                }
                if (inputEditText.text.isNotEmpty()) {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        refreshButton.setOnClickListener {
            searchTracks()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && inputEditText.text.isEmpty() && trackHistoryAdapter.itemCount != 0) {
                    progressBar.visibility = View.VISIBLE
                    searchHistory.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    searchHistory.visibility = View.GONE
                }
        }

        clearHistoryButton.setOnClickListener {
            trackHistoryAdapter.updateData(listOf())
            sharedPreferences.edit().clear().apply()
            searchHistory.visibility = View.GONE
        }
    }

    private fun searchTracks() {
        progressBar.visibility = View.VISIBLE
        rvTrack.visibility = View.GONE
        apiService.searchTracks(actualInput).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    progressBar.visibility = View.GONE
                    tracks = body.results
                    //оставляем только треки у аудио книг нет trackId
                    tracks = tracks.mapNotNull { track ->
                        if (track.trackId != null) {
                            track
                        } else {
                            null
                        }
                    }
                    if (tracks.isEmpty()) {
                        trackAdapter.clear()
                        rvTrack.visibility = View.GONE
                        errorElement.visibility = View.VISIBLE
                        errorImage.setImageResource(R.drawable.track_not_found)
                        errorText.setText(R.string.nothing_found)
                        refreshButton.visibility = View.GONE
                        return
                    }
                    trackAdapter.updateData(tracks)
                    rvTrack.visibility = View.VISIBLE
                    errorElement.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                trackAdapter.clear()
                rvTrack.visibility = View.GONE
                errorElement.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.track_search_error)
                errorText.setText(R.string.connection_error)
                refreshButton.visibility = View.VISIBLE
                Log.e("Retrofit", "Request failed", t)
            }
        })
    }

    private val searchRunnable = Runnable { searchTracks() }
    private fun searchDebounce() {
        handler?.removeCallbacks(searchRunnable)
        handler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_KEY, actualInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        actualInput = savedInstanceState.getString(INPUT_KEY, "")
        inputEditText.setText(actualInput)
        inputEditText.setSelection(actualInput.length)
    }

    private fun readTracksFromPrefs(): List<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            GsonProvider.gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    private fun writeTracksToPrefs(tracks: Array<Track>) {
        val json = GsonProvider.gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    private fun writeTrackToPrefs(track: Track) {
        val json = GsonProvider.gson.toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_TO_AUDIO_PLAYER_KEY, json)
            .apply()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler?.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}