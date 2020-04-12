package se.hyena.eclipse.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_friends.*

import se.hyena.eclipse.R
import se.hyena.eclipse.util.FirestoreUtil


class FriendsFragment : Fragment() {

    private lateinit var userListernerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userListernerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecycleView)

        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListernerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecycleView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@FriendsFragment.context)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() {

        }

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()
    }

}
