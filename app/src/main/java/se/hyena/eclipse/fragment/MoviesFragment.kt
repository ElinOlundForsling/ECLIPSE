package se.hyena.eclipse.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_movies.*
import se.hyena.eclipse.*

import se.hyena.eclipse.model.MoviesRepository
import se.hyena.eclipse.recyclerview.item.WatchlistItem
import se.hyena.eclipse.util.FirestoreUtil


class MoviesFragment : Fragment() {

    private lateinit var movieSearchField: EditText
    private lateinit var movieResultRecyclerView: RecyclerView

    private var searchResultPage = 1

    private var shouldInitSearchResultView = true
    private lateinit var moviesSection: Section


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieSearchField = view.findViewById(R.id.search_bar_movies)
        movieResultRecyclerView = view.findViewById(R.id.recyclerview_search_results_movies)

        movieSearchField.setOnEditorActionListener { v, actionId, event ->
            Log.i("I'm", "trigged")
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchText = movieSearchField.text.toString()
                val context = this.context
                Log.i("SearchText:", searchText)
                shouldInitSearchResultView = true
                if (context != null) {
                    getSearchResultMovies(searchText, context)
                }
                true
            } else {
                false
            }
        }
    }

    private fun getSearchResultMovies(query: String, context: Context) {
        MoviesRepository.getSearchResults(
            searchResultPage,
            query,
            context,
            ::updateRecycleView,
            ::onError
        )
    }

    private fun updateRecycleView(items: List<Item>) {

        fun init() {
            recyclerview_search_results_movies.apply {
                layoutManager = LinearLayoutManager(this@MoviesFragment.context)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    moviesSection = Section(items)
                    add(moviesSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitSearchResultView = false
        }

        fun updateItems() = moviesSection.update(items)

        if (shouldInitSearchResultView)
            init()
        else
            updateItems()
    }

    private fun onError() {
        Toast.makeText(this.context, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is WatchlistItem) {
            val chatIntent = Intent(this.context, MovieDetailsActivity::class.java)
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
