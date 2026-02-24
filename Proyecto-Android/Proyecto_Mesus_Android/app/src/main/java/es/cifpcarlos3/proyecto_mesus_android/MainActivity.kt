package es.cifpcarlos3.proyecto_mesus_android

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.fragments.ViewTogglable
import es.cifpcarlos3.proyecto_mesus_android.fragments.CollectionFragment
import es.cifpcarlos3.proyecto_mesus_android.fragments.CollectionDetailFragment
import es.cifpcarlos3.proyecto_mesus_android.fragments.EventsFragment
import es.cifpcarlos3.proyecto_mesus_android.fragments.UserSearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fab = findViewById<View>(R.id.float_button)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.collectionFragment, R.id.eventsFragment, R.id.chatListFragment),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
        navigationView.setupWithNavController(navController)

        setupMenu()

        val sharedPrefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            findViewById<View>(android.R.id.content).post {
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    navController.navigate(R.id.action_loginFragment_to_collectionFragment)
                }
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    sharedPrefs.edit().apply {
                        putBoolean("isLoggedIn", false)
                        remove("username")
                        apply()
                    }
                    navController.navigate(R.id.loginFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_search_users -> {
                    navController.navigate(R.id.userSearchFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    val handled = onNavDestinationSelected(menuItem, navController)
                    if (handled) drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment,
                R.id.addCardFragment, R.id.addEventFragment,
                R.id.addCollectionFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    toolbar.visibility = View.GONE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.userSearchFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.collectionDetailFragment -> {
                    val bundle = navController.currentBackStackEntry?.arguments
                    val username = bundle?.getString("username")
                    val collectionId = bundle?.getInt("collectionId") ?: -1
                    val currentCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        bundle?.getSerializable("coleccion", Coleccion::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        bundle?.getSerializable("coleccion") as? Coleccion
                    }

                    bottomNavigationView.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    
                    if (username != null) {
                        fab.visibility = View.GONE
                    } else {
                        fab.visibility = View.VISIBLE
                        fab.setOnClickListener { 
                            val bundle = Bundle().apply {
                                putInt("collectionId", collectionId)
                                putSerializable("coleccion", currentCollection)
                                putSerializable("carta", null)
                            }
                            navController.navigate(R.id.addCardFragment, bundle)
                        }
                    }
                }
                R.id.eventDetailFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.chatListFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.chatDetailFragment -> {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.collectionFragment -> {
                    val bundle = navController.currentBackStackEntry?.arguments
                    val username = bundle?.getString("username")
                    
                    if (username != null) {
                        bottomNavigationView.visibility = View.GONE
                        fab.visibility = View.GONE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    } else {
                        bottomNavigationView.visibility = View.VISIBLE
                        fab.visibility = View.VISIBLE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        fab.setOnClickListener { navController.navigate(R.id.addCollectionFragment) }
                    }
                    toolbar.visibility = View.VISIBLE
                }
                R.id.eventsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    fab.setOnClickListener { navController.navigate(R.id.addEventFragment) }
                }

                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    fab.setOnClickListener(null)
                }
            }
            invalidateOptionsMenu()
        }
    }

    private fun setupMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

                searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
                        val currentDestId = navHostFragment?.navController?.currentDestination?.id

                        when {
                            currentFragment is CollectionFragment || currentDestId == R.id.collectionFragment ->
                                (currentFragment as? CollectionFragment)?.filterCollections(newText)
                            currentFragment is CollectionDetailFragment || currentDestId == R.id.collectionDetailFragment ->
                                (currentFragment as? CollectionDetailFragment)?.filterCards(newText)
                            currentFragment is EventsFragment || currentDestId == R.id.eventsFragment ->
                                (currentFragment as? EventsFragment)?.filterEvents(newText)
                            currentFragment is UserSearchFragment || currentDestId == R.id.userSearchFragment -> {
                                val fragment = (currentFragment as? UserSearchFragment)
                                    ?: navHostFragment?.childFragmentManager?.fragments?.find { it is UserSearchFragment } as? UserSearchFragment
                                fragment?.searchUsers(newText)
                            }
                        }
                        return true
                    }
                })
            }

            override fun onPrepareMenu(menu: Menu) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                
                val toggleItem = menu.findItem(R.id.action_toggle_view)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

                val currentDestId = navController.currentDestination?.id

                when {
                    currentDestId == R.id.collectionFragment -> {
                        searchItem?.isVisible = true
                        searchView?.queryHint = (currentFragment as? CollectionFragment)?.getSearchHint() ?: getString(R.string.buscarColeccionHint)
                    }
                    currentDestId == R.id.collectionDetailFragment -> {
                        searchItem?.isVisible = true
                        searchView?.queryHint = (currentFragment as? CollectionDetailFragment)?.getSearchHint() ?: getString(R.string.buscarCartaHint)
                    }
                    currentDestId == R.id.eventsFragment -> {
                        searchItem?.isVisible = true
                        searchView?.queryHint = (currentFragment as? EventsFragment)?.getSearchHint() ?: getString(R.string.buscarEventoHint)
                    }
                    currentDestId == R.id.userSearchFragment -> {
                        searchItem?.isVisible = true
                        searchItem?.expandActionView()
                        searchView?.queryHint = (currentFragment as? UserSearchFragment)?.getSearchHint() ?: "Buscar usuario..."
                    }
                    else -> {
                        searchItem?.isVisible = false
                    }
                }

                if (currentFragment is ViewTogglable) {
                    toggleItem?.isVisible = true
                    val isListView = currentFragment.isListView()
                    toggleItem?.setIcon(if (isListView) R.drawable.ic_map else R.drawable.ic_view_list)
                } else {
                    toggleItem?.isVisible = false
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_toggle_view -> {
                        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                        if (currentFragment is ViewTogglable) {
                            currentFragment.toggleView()
                            invalidateOptionsMenu()
                        }
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
