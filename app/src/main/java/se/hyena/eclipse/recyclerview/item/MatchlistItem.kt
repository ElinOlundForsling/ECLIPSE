package se.hyena.eclipse.recyclerview.item

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_matchlist.*
import kotlinx.android.synthetic.main.item_watchlist.*
import se.hyena.eclipse.R
import se.hyena.eclipse.model.Movie


class MatchlistItem (
    val movie: Movie,
    val movieId: String,
    private val context: Context
)
    : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_movie_title.text = movie.title
        viewHolder.text_view_movie_overview.text = movie.overview
        viewHolder.rating_bar_movie_rating.rating = movie.rating / 2

        if (movie.posterPath != "")
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .transform(CenterCrop())
                .placeholder(R.drawable.ic_poster_placeholder)
                .into(viewHolder.image_view_match_movie_poster)

    }

    override fun getLayout() = R.layout.item_matchlist
}