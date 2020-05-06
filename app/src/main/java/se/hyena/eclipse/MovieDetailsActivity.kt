package se.hyena.eclipse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.firestore.ListenerRegistration
import se.hyena.eclipse.model.Movie
import se.hyena.eclipse.util.FirestoreUtil

const val MOVIE_BACKDROP = "extra_movie_backdrop"
const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_RATING = "extra_movie_rating"
const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
const val MOVIE_OVERVIEW = "extra_movie_overview"
const val MOVIE_ID = "extra_movie_id"

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var overview: TextView
    private lateinit var addToWatchList: Button
    private lateinit var removeFromWatchList: Button
    private lateinit var viewFlipper: ViewFlipper

    private lateinit var isAddedListenerRegistration: ListenerRegistration

    private var movieId = 0L
    private var movieBackdrop = ""
    private var moviePoster = ""
    private var movieTitle = ""
    private var movieRating = 0f
    private var movieReleaseDate = ""
    private var movieOverview = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        backdrop = findViewById(R.id.movie_backdrop)
        poster = findViewById(R.id.movie_poster)
        title = findViewById(R.id.movie_title)
        rating = findViewById(R.id.movie_rating)
        overview = findViewById(R.id.movie_overview)
        addToWatchList = findViewById(R.id.add_to_watchlist)
        removeFromWatchList = findViewById(R.id.remove_from_watchlist)
        viewFlipper = findViewById(R.id.view_flipper_movie_details)



        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }

        isAddedListenerRegistration = FirestoreUtil.doesMovieExistListener(movieId, this::changeButton)
    }

    private fun populateDetails(extras: Bundle) {

        movieId = extras.getLong(MOVIE_ID)
        movieBackdrop = extras.getString(MOVIE_BACKDROP, "")
        moviePoster = extras.getString(MOVIE_POSTER, "")
        movieTitle = extras.getString(MOVIE_TITLE, "")
        movieRating = extras.getFloat(MOVIE_RATING, 0f)
        movieReleaseDate = extras.getString(MOVIE_RELEASE_DATE, "")
        movieOverview = extras.getString(MOVIE_OVERVIEW, "")

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w1280$movieBackdrop")
            .transform(CenterCrop())
            .into(backdrop)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342$moviePoster")
            .transform(CenterCrop())
            .into(poster)

        title.text = movieTitle
        rating.rating = movieRating / 2
        overview.text = movieOverview


    }

    private fun changeButton(doesExist: Boolean) {
        if (doesExist) {
            viewFlipper.displayedChild = 1
        } else {
            viewFlipper.displayedChild = 0
        }
    }

    override fun onStart() {
        super.onStart()
        addToWatchList.setOnClickListener {
            val addMovie = Movie(movieId, movieTitle, movieOverview, moviePoster, movieBackdrop, movieRating, movieReleaseDate)
            FirestoreUtil.addMovieToWatchlist(movieId, addMovie) { movieTitle ->
                val toast = Toast.makeText(this, "$movieTitle added to watchlist!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        removeFromWatchList.setOnClickListener {
            FirestoreUtil.removeFromWatchlist(movieId)
        }
    }
}
