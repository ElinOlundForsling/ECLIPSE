package se.hyena.eclipse.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import se.hyena.eclipse.recyclerview.item.SearchItem


class MoviesFragment : Fragment() {

    private lateinit var movieSearchField: EditText
    private lateinit var movieLinearLayout: LinearLayout
    private lateinit var movieResultRecyclerView: RecyclerView
    private lateinit var movieViewFlipper: ViewFlipper
    private lateinit var movieNoData: TextView
    private lateinit var movieResult: TextView

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
        movieLinearLayout = view.findViewById(R.id.linear_layout_search_results_movies)
        movieViewFlipper = view.findViewById(R.id.view_flipper_movies_result)
        movieNoData = view.findViewById(R.id.text_view_movies_no_data)
        movieResult = view.findViewById(R.id.text_view_search_result_result)

        movieLinearLayout.isGone = true

        movieSearchField.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchText = movieSearchField.text.toString()
                val searchTitle = "Search Results for: $searchText"
                movieResult.text = searchTitle
                val context = this.context
                shouldInitSearchResultView = true
                if (context != null) {
                    getSearchResultMovies(searchText, context)
                }
                movieSearchField.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        movieLinearLayout.isGone = true
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

        movieLinearLayout.isVisible = true

        if (items.isEmpty()) {
            movieViewFlipper.displayedChild = 1
        } else {
            movieViewFlipper.displayedChild = 0
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
    }

    private fun onError() {
        Toast.makeText(this.context, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is SearchItem) {
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
