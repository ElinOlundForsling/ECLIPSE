package se.hyena.eclipse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_movie_match.*
import se.hyena.eclipse.glide.GlideApp
import se.hyena.eclipse.recyclerview.item.MatchlistItem
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil

class MovieMatchActivity : AppCompatActivity() {

    private lateinit var matchlistListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var movieSection: Section

    private lateinit var matchlistRecyclerView: RecyclerView
    private lateinit var matchlistUserPicture: ImageView
    private lateinit var matchlistFriendPicture: ImageView
    private lateinit var matchlistFriendName: TextView

    private lateinit var idExtra: String
    private lateinit var nameExtra: String
    private lateinit var profilePicturePathExtra: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_match)

        matchlistRecyclerView = findViewById(R.id.recycler_view_match_movies)
        matchlistUserPicture = findViewById(R.id.image_view_user_picture_match)
        matchlistFriendPicture = findViewById(R.id.image_view_friend_picture_match)
        matchlistFriendName = findViewById(R.id.text_view_match_movies_name)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }

        FirestoreUtil.getCurrentUser { user ->
            if (user.profilePath != null)
                GlideApp.with(this)
                    .load(StorageUtil.pathToReference(user.profilePath))
                    .placeholder(R.drawable.ic_menu_profile)
                    .into(matchlistUserPicture)
        }

        menu_bottom_movie_match.setOnItemSelectedListener { id ->
            val intent = Intent(this, MainActivity::class.java)
            when (id) {
                R.id.home -> {
                    intent.apply {
                        putExtra(FRAGMENT, HOME_FRAGMENT)
                    }
                }
                R.id.friends -> {
                    intent.apply {
                        putExtra(FRAGMENT, FRIENDS_FRAGMENT)
                    }
                }
                R.id.search -> {
                    intent.apply {
                        putExtra(FRAGMENT, SEARCH_FRAGMENT)
                    }
                }
                R.id.profile -> {
                    intent.apply {
                        putExtra(FRAGMENT, ACCOUNT_FRAGMENT)
                    }
                }
            }
            startActivity(intent)
        }
    }

    private fun populateDetails(extras: Bundle) {
        nameExtra = extras.getString(FRIEND_NAME, "")
        profilePicturePathExtra = extras.getString(FRIEND_PROFILE_PICTURE_PATH, "")
        idExtra = extras.getString(FRIEND_ID, "")

        Glide.with(this)
            .load(StorageUtil.pathToReference(profilePicturePathExtra))
            .transform(CenterCrop())
            .into(matchlistFriendPicture)


        matchlistFriendName.text = nameExtra
        matchlistListenerRegistration = FirestoreUtil.addMatchlistListener(this, idExtra, this::updateRecyclerView)
    }

    private fun updateRecyclerView(items: List<Item>) {

        if (items.isEmpty()) {
            view_flipper_movie_match.displayedChild = 1
        } else {
            view_flipper_movie_match.displayedChild = 0
            fun init() {
                recycler_view_match_movies.apply {
                    layoutManager = LinearLayoutManager(this@MovieMatchActivity)
                    adapter = GroupAdapter<GroupieViewHolder>().apply {
                        movieSection = Section(items)
                        add(movieSection)
                        setOnItemClickListener(onItemClick)
                    }
                }
                shouldInitRecyclerView = false
            }

            fun updateItems() = movieSection.update(items)

            if (shouldInitRecyclerView)
                init()
            else
                updateItems()
        }
    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is MatchlistItem) {
            val chatIntent = Intent(this, MovieDetailsActivity::class.java)
                .apply {
                    putExtra(MOVIE_ID, item.movie.id)
                    putExtra(MOVIE_TITLE, item.movie.title)
                    putExtra(MOVIE_BACKDROP, item.movie.backdropPath)
                    putExtra(MOVIE_POSTER, item.movie.posterPath)
                    putExtra(MOVIE_OVERVIEW, item.movie.overview)
                    putExtra(MOVIE_RATING, item.movie.rating)
                    putExtra(MOVIE_RELEASE_DATE, item.movie.releaseDate)
                }
            startActivity(chatIntent)
        }

    }
}
