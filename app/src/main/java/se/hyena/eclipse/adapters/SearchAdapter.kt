package se.hyena.eclipse.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import se.hyena.eclipse.model.Search
import se.hyena.eclipse.MovieDetailsActivity
import se.hyena.eclipse.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchAdapter(val context: Context, val namelist:List<Search>, val check:Boolean): RecyclerView.Adapter<SearchAdapter.myviewholder>() {

    val baseURL = "https://image.tmdb.org/t/p/w780/"


    override fun getItemCount(): Int {
        if(check==false)
            if (namelist != null) {
                return namelist.size

            }

        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {

        var li=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView=li.inflate(R.layout.item_search,parent,false)
        return myviewholder(itemView)

    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {
        val item1= this.namelist[position]


        holder.itemView.text_view_search_result.text = item1.original_title

        var target : String

        target = item1.poster_path

        Picasso.get().load(baseURL + target).into(holder.itemView.image_view_search_result)




        holder.itemView.parentLayout.setOnClickListener {

            val intent= Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("id",item1.id)
            intent.putExtra("type","movie")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    inner class myviewholder(itemView:View):RecyclerView.ViewHolder(itemView)
}