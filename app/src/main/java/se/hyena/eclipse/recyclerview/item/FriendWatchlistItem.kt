package se.hyena.eclipse.recyclerview.item

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_watchlist.*
import se.hyena.eclipse.R
import se.hyena.eclipse.model.Movie


class FriendWatchlistItem (
    val movie: Movie,
    val movieId: String,
    private val context: Context
)
    : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_title.text = movie.title
        viewHolder.text_view_release_year.text = movie.releaseDate
        if (movie.posterPath != null)
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .transform(CenterCrop())
                .into(viewHolder.image_view_poster)

    }

    override fun getLayout() = R.layout.item_watchlist
}