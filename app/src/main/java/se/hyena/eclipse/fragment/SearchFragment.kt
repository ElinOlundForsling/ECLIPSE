package se.hyena.eclipse.fragment

import TabAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_search.*
import se.hyena.eclipse.R


/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewpager)
        viewpager?.adapter = TabAdapter(this.activity)
        tabLayout = view.findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Movies"
                1 -> tab.text = "TV Shows"
                else -> tab.text = "Movies"
            }
            //tab.text = "Tab $position"
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

}
