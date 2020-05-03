package se.hyena.eclipse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_friend_profile.*
import kotlinx.android.synthetic.main.activity_movie_match.*
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil

class MovieMatchActivity : AppCompatActivity() {

    private lateinit var matchlistListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var movieSection: Section

    private lateinit var matchlistRecyclerView: RecyclerView

    private lateinit var idExtra: String
    private lateinit var nameExtra: String
    private lateinit var profilePicturePathExtra: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_match)

        matchlistRecyclerView = findViewById(R.id.recycler_view_match_movies)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }
    }

    private fun populateDetails(extras: Bundle) {
        nameExtra = extras.getString(FRIEND_NAME, "")
        profilePicturePathExtra = extras.getString(FRIEND_PROFILE_PICTURE_PATH, "")
        idExtra = extras.getString(FRIEND_ID, "")
        //matchlistTitleExtra = extras.getString(FRIEND_NAME, "") + "'s Watchlist"

        /*Glide.with(this)
            .load(StorageUtil.pathToReference(friendProfilePicturePathExtra))
            .transform(CenterCrop())
            .into(friendProfilePicture)

         */

        //friendWatchlistTitle.text = friendWatchlistTitleExtra
        matchlistListenerRegistration = FirestoreUtil.addMatchlistListener(this, idExtra, this::updateRecyclerView)
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_match_movies.apply {
                layoutManager = LinearLayoutManager(this@MovieMatchActivity)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    movieSection = Section(items)
                    add(movieSection)
                    //setOnItemClickListener(onItemClick)
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
