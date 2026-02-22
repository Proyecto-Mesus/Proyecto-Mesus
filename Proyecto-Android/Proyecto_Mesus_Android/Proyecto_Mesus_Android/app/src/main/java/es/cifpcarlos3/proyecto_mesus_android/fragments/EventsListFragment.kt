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
    private lateinit var adapter: EventAdapter

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
        
        adapter = EventAdapter(
            emptyList(),
            onItemClick = { event ->
                val bundle = Bundle().apply {
                    putSerializable("evento", event)
                }
                findNavController().navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle)
            },
            onLongClick = { event, view ->
                if (showOnlyMine) {
                    val popup = androidx.appcompat.widget.PopupMenu(requireContext(), view)
                    popup.menuInflater.inflate(R.menu.menu_context_item, popup.menu)
                    
                    popup.setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.action_edit -> {
                                val bundle = Bundle().apply {
                                    putSerializable("evento", event)
                                }
                                findNavController().navigate(R.id.addEventFragment, bundle)
                                true
                            }
                            R.id.action_delete -> {
                                com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(getString(R.string.eliminarEvento))
                                    .setMessage(getString(R.string.confirmarEliminarEvento, event.nombre))
                                    .setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                                        viewModel.deleteEvento(event.idEvento)
                                        com.google.android.material.snackbar.Snackbar.make(binding.root, getString(R.string.eventoEliminado), com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                                    }
                                    .setNegativeButton(getString(R.string.cancelar), null)
                                    .show()
                                true
                            }
                            else -> false
                        }
                    }
                    popup.show()
                }
            }
        )
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is EventoUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is EventoUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.updateList(state.list)
                        }
                        is EventoUiState.ActionSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            if (showOnlyMine) {
                                val userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE).getInt("userId", -1)
                                viewModel.fetchMyEvents(userId)
                            } else {
                                viewModel.fetchEvents()
                            }
                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        requireActivity().addMenuProvider(object : androidx.core.view.MenuProvider {
            override fun onCreateMenu(menu: android.view.Menu, menuInflater: android.view.MenuInflater) {}

            override fun onPrepareMenu(menu: android.view.Menu) {
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView
                
                searchView?.queryHint = getString(R.string.buscarEventoHint)
                searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
