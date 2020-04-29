package se.hyena.eclipse.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import se.hyena.eclipse.R
import se.hyena.eclipse.model.WatchList

class WatchListAdapter(
    private var items: List<WatchList>
) : RecyclerView.Adapter<WatchListAdapter.WatchListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_watchlist, parent, false)
        return WatchListHolder(view)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: WatchListHolder, position: Int) {
        holder.bind(items[position])
    }
    fun updateItems(items: List<WatchList>) {
        this.items = items
        notifyDataSetChanged()
    }
    inner class WatchListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.image_view_poster)
        fun bind(item: WatchList) {
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w342${item.posterPath}")
                .transform(CenterCrop())
                .into(poster)
        }
    }
}