package com.group.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.group.movieapp.ui.theme.MovieAppTheme
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5NWQ2YTBmM2E0YjUxYmJiMDE1OWYwZjQ1NGQ3MDI4MiIsIm5iZiI6MTcyMzUwODI1NC41MzM0ODMsInN1YiI6IjY2YmFhNTc5M2UyZWU5ZTdhY2I3NmNmYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.plTqGIypZZYIFA_--Mf0GyaWmOvvRhb7zXNfoo8TsMk" // Replace with your TMDB API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MovieScreen(apiKey)
                }
            }
        }
    }
}


@Composable
fun MovieScreen(apiKey: String) {
    var movieTitle by remember { mutableStateOf("Loading...") }
    var movieOverview by remember { mutableStateOf("") }

    // Initialize Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()
    val movieApi = retrofit.create(TMDBApi::class.java)

    LaunchedEffect(Unit) {
        movieApi.searchMovies("Toronto Weather", apiKey).enqueue(object : retrofit2.Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: retrofit2.Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    if (movies != null && movies.isNotEmpty()) {
                        movieTitle = movies[0].title
                        movieOverview = movies[0].overview
                    } else {
                        movieTitle = "No results found"
                    }
                } else {
                    movieTitle = "Error fetching data"
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                movieTitle = "Network error"
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Title: $movieTitle", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Overview: $movieOverview", style = MaterialTheme.typography.bodyMedium)
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieAppTheme {
        MovieScreen("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5NWQ2YTBmM2E0YjUxYmJiMDE1OWYwZjQ1NGQ3MDI4MiIsIm5iZiI6MTcyMzUwODI1NC41MzM0ODMsInN1YiI6IjY2YmFhNTc5M2UyZWU5ZTdhY2I3NmNmYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.plTqGIypZZYIFA_--Mf0GyaWmOvvRhb7zXNfoo8TsMk")
    }
}