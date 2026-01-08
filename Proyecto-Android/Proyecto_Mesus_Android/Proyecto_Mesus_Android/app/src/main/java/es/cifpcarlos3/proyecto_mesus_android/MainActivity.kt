package es.cifpcarlos3.proyecto_mesus_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fab = findViewById<View>(R.id.float_button)

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment,
                R.id.addCardFragment, R.id.addEventFragment, R.id.addMarketplaceItemFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                }
                R.id.chatListFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                }
                R.id.collectionFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    fab.setOnClickListener { navController.navigate(R.id.addCardFragment) }
                }
                R.id.eventsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    fab.setOnClickListener { navController.navigate(R.id.addEventFragment) }
                }
                R.id.marketplaceFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    fab.setOnClickListener { navController.navigate(R.id.addMarketplaceItemFragment) }
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    fab.setOnClickListener(null)
                }
            }
        }
    }
}
