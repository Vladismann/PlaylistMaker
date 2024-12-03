package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
        private const val INPUT_KEY = "ACTUAL_TEXT"
    }

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
    private lateinit var tracks: MutableList<Track>
    private lateinit var errorElement: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var rvTrack: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        inputEditText = findViewById(R.id.input_search)
        errorElement = findViewById(R.id.errorMessageElement)
        refreshButton = findViewById(R.id.refreshTracks)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        tracks = arrayListOf()
        trackAdapter = TrackAdapter(tracks)
        rvTrack = findViewById(R.id.rvTrack)
        rvTrack.adapter = trackAdapter

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
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            }
            false
        }

        refreshButton.setOnClickListener {
            searchTracks()
        }
    }

    private fun searchTracks() {
        apiService.searchTracks(actualInput).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {
                    tracks = response.body()!!.results
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

}
