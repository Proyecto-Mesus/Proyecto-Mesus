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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.adapters.EventAdapter
import es.cifpcarlos3.proyecto_mesus_android.databinding.FragmentListBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventsViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventoUiState
import kotlinx.coroutines.launch

class EventsListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private val viewModel: EventsViewModel by viewModels()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is EventoUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is EventoUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.adapter = EventAdapter(state.list) { event ->
                                val bundle = Bundle().apply {
                                    putSerializable("evento", event)
                                }
                                findNavController().navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle)
                            }
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        if (showOnlyMine) {
            val sharedPrefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("userId", -1)
            viewModel.fetchMyEvents(userId)
        } else {
            viewModel.fetchEvents()
        }
    }

    companion object {
        private const val ARG_SHOW_ONLY_MINE = "show_only_mine"

        fun newInstance(showOnlyMine: Boolean) = EventsListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_SHOW_ONLY_MINE, showOnlyMine)
            }
        }
    }
}
