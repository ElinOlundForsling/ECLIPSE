package se.hyena.eclipse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import se.hyena.eclipse.fragment.AccountFragment
import se.hyena.eclipse.fragment.FriendsFragment
import se.hyena.eclipse.fragment.HomeFragment
import se.hyena.eclipse.fragment.SearchFragment

class MainActivity : AppCompatActivity() {

    lateinit var homeFragment: HomeFragment
    lateinit var accountFragment: AccountFragment
    lateinit var searchFragment: SearchFragment
    lateinit var friendsFragment: FriendsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu_bottom.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
                R.id.friends -> {
                    friendsFragment = FriendsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, friendsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.search -> {
                    searchFragment = SearchFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.profile -> {
                    accountFragment = AccountFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, accountFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
        }
    }
}
