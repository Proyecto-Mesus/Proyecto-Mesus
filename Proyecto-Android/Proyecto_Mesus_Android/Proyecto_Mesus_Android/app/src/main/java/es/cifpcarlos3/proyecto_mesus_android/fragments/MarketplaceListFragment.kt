package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.adapters.MarketplaceAdapter
import es.cifpcarlos3.proyecto_mesus_android.databinding.FragmentListBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MarketplaceViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MercadoUiState
import kotlinx.coroutines.launch

class MarketplaceListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private val viewModel: MarketplaceViewModel by viewModels()
    private var showOnlyMine: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            showOnlyMine = it.getBoolean(ARG_SHOW_ONLY_MINE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        
        observeViewModel()

        if (showOnlyMine) {
            val sharedPrefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("userId", -1)
            viewModel.fetchMyItems(userId)
        } else {
            viewModel.fetchItems()
        }
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
                            binding.recyclerView.adapter = MarketplaceAdapter(state.list)
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_SHOW_ONLY_MINE = "show_only_mine"

        fun newInstance(showOnlyMine: Boolean) = MarketplaceListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_SHOW_ONLY_MINE, showOnlyMine)
            }
        }
    }
}
