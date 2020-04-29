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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(HomeFragment())

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
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
