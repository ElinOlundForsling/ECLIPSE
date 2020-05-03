package se.hyena.eclipse.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.item_friend.view.*
import se.hyena.eclipse.AppConstants
import se.hyena.eclipse.ChatActivity
import se.hyena.eclipse.R
import se.hyena.eclipse.model.User
import se.hyena.eclipse.recyclerview.item.FriendItem
import se.hyena.eclipse.recyclerview.item.PersonItem
import se.hyena.eclipse.util.FirestoreUtil


class FriendsFragment : Fragment() {

    private lateinit var userListernerRegistration: ListenerRegistration
    private lateinit var searchResultListernerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true
    private var shouldInitSearchView = true

    private lateinit var peopleSection: Section
    private lateinit var resultSection: Section

    private lateinit var searchBar: EditText
    private lateinit var searchResults: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        userListernerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecycleView)

        searchBar = view.findViewById(R.id.search_bar_users)
        searchResults = view.findViewById(R.id.recycler_view_search_results_users)

        searchBar.setOnEditorActionListener { v, actionId, event ->
            Log.i("I'm", "trigged")
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchText = searchBar.text.toString()
                Log.i("SearchText:", searchText)
                shouldInitSearchView = true
                searchResultListernerRegistration = FirestoreUtil.addSearchResultListener(this.activity!!, searchText, this::updateSearchView)
                true
            } else {
                false
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListernerRegistration)
        if (::searchResultListernerRegistration.isInitialized) {
            FirestoreUtil.removeListener(searchResultListernerRegistration)
        }

        shouldInitRecyclerView = true
        shouldInitSearchView = true
    }

    private fun updateRecycleView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@FriendsFragment.context)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()
    }

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is PersonItem) {
            val chatIntent = Intent(this.context, ChatActivity::class.java)
                .apply {
                    putExtra(AppConstants.USER_NAME, item.person.name)
                    putExtra(AppConstants.USER_ID, item.userId)
                }
            startActivity(chatIntent)
        }

    }

    private fun updateSearchView(items: List<Item>) {

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
