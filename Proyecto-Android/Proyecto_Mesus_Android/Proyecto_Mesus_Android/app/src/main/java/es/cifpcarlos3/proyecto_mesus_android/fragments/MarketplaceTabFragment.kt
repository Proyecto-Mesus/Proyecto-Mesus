package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import es.cifpcarlos3.proyecto_mesus_android.databinding.FragmentTabContainerBinding
import es.cifpcarlos3.proyecto_mesus_android.R

class MarketplaceTabFragment : Fragment() {
    private lateinit var binding: FragmentTabContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MarketplacePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_all)
                1 -> getString(R.string.tab_my_sales)
                else -> ""
            }
        }.attach()
    }
}

class MarketplacePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MarketplaceListFragment.newInstance(false)
            1 -> MarketplaceListFragment.newInstance(true)
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
