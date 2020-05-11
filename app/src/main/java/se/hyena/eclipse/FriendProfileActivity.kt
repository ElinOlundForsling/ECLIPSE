package se.hyena.eclipse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
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
import kotlinx.android.synthetic.main.activity_friend_profile.*
import se.hyena.eclipse.model.Friend
import se.hyena.eclipse.recyclerview.item.FriendWatchlistItem
import se.hyena.eclipse.util.FirestoreUtil
import se.hyena.eclipse.util.StorageUtil

const val FRIEND_ID = "extra_friend_id"
const val FRIEND_NAME = "extra_friend_name"
const val FRIEND_BIO = "extra_friend_bio"
const val FRIEND_PROFILE_PICTURE_PATH = "extra_friend_profile_picture_path"
const val FRIEND_MOVIE_LIST = "extra_friend_movie_list"

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var friendName: TextView
    private lateinit var friendBio: TextView
    private lateinit var friendWatchlistTitle: TextView
    private lateinit var friendProfilePicture: ImageView
    private lateinit var friendWatchlist: RecyclerView
    private lateinit var friendChatButton: Button
    private lateinit var friendMatchButton: Button
    private lateinit var friendViewFlipperButton: ViewFlipper
    private lateinit var friendBefriendButton: Button
    private lateinit var friendUnfriendButton: Button
    private lateinit var friendNoData: TextView
    private lateinit var friendViewFlipperView: ViewFlipper

    private lateinit var friendIdExtra: String
    private lateinit var friendNameExtra: String
    private lateinit var friendBioExtra: String
    private lateinit var friendProfilePicturePathExtra: String
    private lateinit var friendMovieListExtra: String
    private lateinit var friendWatchlistTitleExtra: String
    private lateinit var friendNoDataExtra: String

    private lateinit var friendWatchlistListenerRegistration: ListenerRegistration
    private lateinit var doesFriendExistListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var movieSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        friendName = findViewById(R.id.text_view_friend_profile_name)
        friendBio = findViewById(R.id.text_view_friend_profile_bio)
        friendProfilePicture = findViewById(R.id.image_view_friend_profile_picture)
        friendWatchlist = findViewById(R.id.recycler_view_friend_profile_watchlist)
        friendWatchlistTitle = findViewById(R.id.text_view_watchlist_title)
        friendChatButton = findViewById(R.id.button_friend_profile_chat)
        friendMatchButton = findViewById(R.id.button_friend_profile_match)
        friendViewFlipperButton = findViewById(R.id.view_flipper_friend_profile)
        friendBefriendButton = findViewById(R.id.button_friend_profile_befriend)
        friendUnfriendButton = findViewById(R.id.button_friend_profile_unfriend)
        friendNoData = findViewById(R.id.text_view_friend_profile_no_data)
        friendViewFlipperView = findViewById(R.id.view_flipper_friend_profile_watchlist)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }

        doesFriendExistListenerRegistration = FirestoreUtil.doesFriendExistListener(friendIdExtra, this::changeButton)

        friendChatButton.setOnClickListener(onChatButtonClick)
        friendMatchButton.setOnClickListener(onMatchButtonClick)
        friendBefriendButton.setOnClickListener(onBefriendButtonClick)
        friendUnfriendButton.setOnClickListener(onUnfriendButtonClick)

    }

    private val onChatButtonClick = View.OnClickListener {
        val chatIntent = Intent(this, ChatActivity::class.java)
            .apply {
                putExtra(AppConstants.USER_NAME, friendNameExtra)
                putExtra(AppConstants.USER_ID, friendIdExtra)
            }
        this.startActivity(chatIntent)
    }

    private val onBefriendButtonClick = View.OnClickListener {
        val newFriend = Friend(friendNameExtra, friendBioExtra, friendProfilePicturePathExtra)
        FirestoreUtil.addFriend(friendIdExtra, newFriend) { friendName ->
            val toast = Toast.makeText(this, "$friendName is added to your friends!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private val onUnfriendButtonClick = View.OnClickListener {
        FirestoreUtil.removeFriend(friendIdExtra)
    }

    private val onMatchButtonClick = View.OnClickListener {
        val matchIntent = Intent(this, MovieMatchActivity::class.java)
            .apply {
                putExtra(FRIEND_ID, friendIdExtra)
                putExtra(FRIEND_NAME, friendNameExtra)
                putExtra(FRIEND_PROFILE_PICTURE_PATH, friendProfilePicturePathExtra)
            }
        this.startActivity(matchIntent)
    }

    private fun populateDetails(extras: Bundle) {

        friendNameExtra = extras.getString(FRIEND_NAME, "")
        friendBioExtra = extras.getString(FRIEND_BIO, "")
        friendProfilePicturePathExtra = extras.getString(FRIEND_PROFILE_PICTURE_PATH, "")
        friendIdExtra = extras.getString(FRIEND_ID, "")
        friendMovieListExtra = extras.getString(FRIEND_MOVIE_LIST, "")
        friendWatchlistTitleExtra = extras.getString(FRIEND_NAME, "") + "'s Watchlist"
        friendNoDataExtra = extras.getString(FRIEND_NAME, "") + " has yet to add any movies to their watchlist."

        Glide.with(this)
            .load(StorageUtil.pathToReference(friendProfilePicturePathExtra))
            .transform(CenterCrop())
            .into(friendProfilePicture)

        friendName.text = friendNameExtra
        friendBio.text = friendBioExtra
        friendWatchlistTitle.text = friendWatchlistTitleExtra

        friendWatchlistListenerRegistration = FirestoreUtil.addFriendWatchlistListener(this, friendIdExtra, this::updateRecyclerView)
    }

    private fun updateRecyclerView(items: List<Item>) {

        if (items.count() == 0) {
            friendNoData.text = friendNoDataExtra
            friendViewFlipperView.displayedChild = 1
        } else {
            friendViewFlipperView.displayedChild = 0
            fun init() {

                recycler_view_friend_profile_watchlist.apply {
                    layoutManager = LinearLayoutManager(this@FriendProfileActivity)
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

    private fun changeButton(doesExist: Boolean) {
        if (doesExist) {
            friendViewFlipperButton.displayedChild = 1
        } else {
            friendViewFlipperButton.displayedChild = 0
        }
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is FriendWatchlistItem) {
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
