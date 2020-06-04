package se.hyena.eclipse.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_friends.*
import se.hyena.eclipse.*
import se.hyena.eclipse.recyclerview.item.FriendItem
import se.hyena.eclipse.recyclerview.item.PersonItem
import se.hyena.eclipse.util.FirestoreUtil
import java.util.*


class FriendsFragment : Fragment() {

    private lateinit var friendListenerRegistration: ListenerRegistration
    private lateinit var searchResultListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true
    private var shouldInitSearchView = true

    private lateinit var friendSection: Section
    private lateinit var resultSection: Section

    private lateinit var searchBar: EditText
    private lateinit var searchResults: RecyclerView
    private lateinit var searchViewFlipper: ViewFlipper
    private lateinit var viewViewFlipper: ViewFlipper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        friendListenerRegistration = FirestoreUtil.addFriendListener(this.requireActivity(), this::updateRecycleView)

        searchBar = view.findViewById(R.id.search_bar_users)
        searchResults = view.findViewById(R.id.recycler_view_search_results_users)
        searchViewFlipper = view.findViewById(R.id.view_flipper_user_search)
        viewViewFlipper = view.findViewById(R.id.view_flipper_friends_view)

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            Log.i("I'm", "triggered")
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchText = searchBar.text.toString().toLowerCase(Locale.ROOT)
                Log.i("SearchText:", searchText)
                shouldInitSearchView = true
                searchResultListenerRegistration = FirestoreUtil.addSearchResultListener(this.requireActivity(), searchText, this::updateSearchView)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(friendListenerRegistration)
        if (::searchResultListenerRegistration.isInitialized) {
            FirestoreUtil.removeListener(searchResultListenerRegistration)
        }

        shouldInitRecyclerView = true
        shouldInitSearchView = true
    }

    private fun updateRecycleView(items: List<Item>) {

        if (items.isEmpty()) {
            viewViewFlipper.displayedChild = 1
        } else {
            viewViewFlipper.displayedChild = 0
            fun init() {
                recycler_view_people.apply {
                    layoutManager = LinearLayoutManager(this@FriendsFragment.context)
                    adapter = GroupAdapter<GroupieViewHolder>().apply {
                        friendSection = Section(items)
                        add(friendSection)
                        setOnItemClickListener(onItemClick)
                    }
                }
                shouldInitRecyclerView = false
            }

            fun updateItems() = friendSection.update(items)

            if (shouldInitRecyclerView)
                init()
            else
                updateItems()
        }
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            val chatIntent = Intent(this.context, FriendProfileActivity::class.java)
                .apply {
                    putExtra(FRIEND_NAME, item.person.name)
                    putExtra(FRIEND_ID, item.userId)
                    putExtra(FRIEND_BIO, item.person.bio)
                    putExtra(FRIEND_PROFILE_PICTURE_PATH, item.person.profilePath)
                }
            startActivity(chatIntent)
        }

        if (item is FriendItem) {
            val chatIntent = Intent(this.context, FriendProfileActivity::class.java)
                .apply {
                    putExtra(FRIEND_NAME, item.person.name)
                    putExtra(FRIEND_ID, item.userId)
                    putExtra(FRIEND_BIO, item.person.bio)
                    putExtra(FRIEND_PROFILE_PICTURE_PATH, item.person.profilePath)
                }
            startActivity(chatIntent)
        }

    }

    private fun updateSearchView(items: List<Item>) {

        if (items.isEmpty()) {
            searchViewFlipper.displayedChild = 1
        } else {
            searchViewFlipper.displayedChild = 0

            fun init() {
                recycler_view_search_results_users.apply {
                    layoutManager = LinearLayoutManager(this@FriendsFragment.context)
                    adapter = GroupAdapter<GroupieViewHolder>().apply {
                        resultSection = Section(items)
                        add(resultSection)
                        setOnItemClickListener(onItemClick)
                    }
                }
                shouldInitSearchView = false
            }

            fun updateItems() = resultSection.update(items)

            if (shouldInitSearchView)
                init()
            else
                updateItems()
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
