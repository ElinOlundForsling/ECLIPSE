package se.hyena.eclipse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import se.hyena.eclipse.fragment.AccountFragment
import se.hyena.eclipse.fragment.FriendsFragment
import se.hyena.eclipse.fragment.HomeFragment
import se.hyena.eclipse.fragment.SearchFragment

const val FRAGMENT = "fragment"
const val HOME_FRAGMENT = "home_fragment"
const val FRIENDS_FRAGMENT = "friends_fragment"
const val SEARCH_FRAGMENT = "search_fragment"
const val ACCOUNT_FRAGMENT = "account_fragment"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras

        if (extras != null) {
            when (extras.getString(FRAGMENT)) {
                HOME_FRAGMENT -> {
                    replaceFragment(HomeFragment())
                    menu_bottom.setItemSelected(R.id.home)
                }
                FRIENDS_FRAGMENT -> {
                    replaceFragment(FriendsFragment())
                    menu_bottom.setItemSelected(R.id.friends)
                }
                SEARCH_FRAGMENT -> {
                    replaceFragment(SearchFragment())
                    menu_bottom.setItemSelected(R.id.search)
                }
                ACCOUNT_FRAGMENT -> {
                    replaceFragment(AccountFragment())
                    menu_bottom.setItemSelected(R.id.profile)
                }
            }
        } else {
            replaceFragment(HomeFragment())
            menu_bottom.setItemSelected(R.id.home)
        }


        menu_bottom.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
            }
                R.id.friends -> {
                    replaceFragment(FriendsFragment())
                }
                R.id.search -> {
                    replaceFragment(SearchFragment())
                }
                R.id.profile -> {
                    replaceFragment(AccountFragment())
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

}
