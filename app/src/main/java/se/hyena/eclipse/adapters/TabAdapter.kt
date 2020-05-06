package se.hyena.eclipse.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import se.hyena.eclipse.fragment.MoviesFragment
import se.hyena.eclipse.fragment.TvShowsFragment

class TabAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    private val eventList = listOf("0", "1")

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MoviesFragment()
            1 -> return TvShowsFragment()
        }
        return MoviesFragment()
    }

    override fun getItemCount(): Int {
        return eventList.count()
    }
}