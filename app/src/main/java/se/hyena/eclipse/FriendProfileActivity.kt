package se.hyena.eclipse

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.android.synthetic.main.fragment_account.*
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
    private lateinit var friendBefriendButton: Button

    private lateinit var friendIdExtra: String
    private lateinit var friendNameExtra: String
    private lateinit var friendBioExtra: String
    private lateinit var friendProfilePicturePathExtra: String
    private lateinit var friendMovieListExtra: String
    private lateinit var friendWatchlistTitleExtra: String

    private lateinit var friendWatchlistListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var movieSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        friendName = findViewById(R.id.text_view_friend_profile_name)
        friendBio = findViewById(R.id.text_view_friend_profile_bio)
        friendProfilePicture = findViewById(R.id.image_view_friend_profile_picture)
        friendWatchlist = findViewById(R.id.recycler_view_friend_profile_watchlist)
        friendWatchlistTitle = findViewById(R.id.text_view_friend_profile_watchlist_title)
        friendChatButton = findViewById(R.id.button_friend_profile_chat)
        friendMatchButton = findViewById(R.id.button_friend_profile_match)
        friendBefriendButton = findViewById(R.id.button_friend_profile_befriend)

        val extras = intent.extras

        if (extras != null) {
            populateDetails(extras)
        } else {
            finish()
        }

        friendChatButton.setOnClickListener(onChatButtonClick)
        friendMatchButton.setOnClickListener(onMatchButtonClick)
        friendBefriendButton.setOnClickListener(onBefriendButtonClick)


    }

    private val onChatButtonClick = View.OnClickListener { view ->
        val chatIntent = Intent(this, ChatActivity::class.java)
            .apply {
                putExtra(AppConstants.USER_NAME, friendNameExtra)
                putExtra(AppConstants.USER_ID, friendIdExtra)
            }
        this.startActivity(chatIntent)
    }

    private val onBefriendButtonClick = View.OnClickListener { view ->
        // TODO: Befriend user
    }

    private val onMatchButtonClick = View.OnClickListener { view ->
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

        fun init() {
            recycler_view_friend_profile_watchlist.apply {
                layoutManager = LinearLayoutManager(this@FriendProfileActivity)
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
