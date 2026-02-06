package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.databinding.MarketplaceFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MarketplaceViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MercadoUiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MarketplaceFragment : Fragment() {
    private lateinit var binding: MarketplaceFragmentBinding
    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MarketplaceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load Tabs Fragment
        if (childFragmentManager.findFragmentByTag("marketplace_tabs") == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.listContainer, MarketplaceTabFragment(), "marketplace_tabs")
                .commit()
        }

        observeViewModel()
        viewModel.fetchItems()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MercadoUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is MercadoUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        is MercadoUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
