package se.hyena.eclipse.recyclerview.item

import android.content.Context
import android.widget.Toast
import android.widget.ViewFlipper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_friend_watchlist.*
import kotlinx.android.synthetic.main.item_watchlist.*
import se.hyena.eclipse.R
import se.hyena.eclipse.model.Movie
import se.hyena.eclipse.util.FirestoreUtil


class FriendWatchlistItem(
    val movie: Movie,
    private val context: Context
)
    : Item() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var doesMovieExistListenerRegistration: ListenerRegistration

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_friend_watchlist_title.text = movie.title
        viewHolder.text_view_friend_watchlist_release_year.text = movie.releaseDate
        if (movie.posterPath != "")
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .transform(CenterCrop())
                .placeholder(R.drawable.ic_poster_placeholder)
                .into(viewHolder.image_view_friend_watchlist_poster)

        viewFlipper = viewHolder.view_flipper_friend_watchlist
        doesMovieExistListenerRegistration = FirestoreUtil.doesMovieExistListener(movie.id, this::changeButton)

        viewHolder.button_add_to_watchlist_friend.setOnClickListener {
            FirestoreUtil.addMovieToWatchlist(movie.id, movie) { movieTitle ->
                val toast = Toast.makeText(this.context, "$movieTitle added to watchlist!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        viewHolder.button_remove_from_watchlist_friend.setOnClickListener {
            FirestoreUtil.removeFromWatchlist(movie.id)
        }
    }

    private fun changeButton(doesExist: Boolean) {
        if (doesExist) {
            viewFlipper.displayedChild = 1
        } else {
            viewFlipper.displayedChild = 0
        }
    }

    override fun getLayout() = R.layout.item_friend_watchlist
}