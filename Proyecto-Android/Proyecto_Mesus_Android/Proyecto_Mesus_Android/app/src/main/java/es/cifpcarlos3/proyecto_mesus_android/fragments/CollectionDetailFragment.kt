package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.adapters.CardAdapter
import androidx.navigation.fragment.findNavController
import es.cifpcarlos3.proyecto_mesus_android.databinding.CollectionDetailFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CollectionDetailViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CartaUiState

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CollectionDetailFragment : Fragment(), ViewTogglable {
    private lateinit var binding: CollectionDetailFragmentBinding
    private val viewModel: CollectionDetailViewModel by viewModels()
    private var collectionId: Int = -1
    private lateinit var adapter: CardAdapter
    override fun isListView(): Boolean = !viewModel.isGridView.value


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionDetailFragmentBinding.inflate(inflater, container, false)
        collectionId = arguments?.getInt("collectionId") ?: -1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CardAdapter(emptyList()) { card, cardView ->
            val userId = requireContext().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE).getInt("userId", -1)
            
            val currentState = viewModel.uiState.value
            if (currentState is CartaUiState.Success && currentState.ownerId == userId) {
                val popup = androidx.appcompat.widget.PopupMenu(requireContext(), cardView)
                popup.menuInflater.inflate(R.menu.menu_context_item, popup.menu)
                
                popup.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.action_edit -> {
                            val bundle = Bundle().apply {
                                putInt("collectionId", collectionId)
                                putSerializable("carta", card)
                            }
                            findNavController().navigate(R.id.addCardFragment, bundle)
                            true
                        }
                        R.id.action_delete -> {
                            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.eliminarCarta))
                                .setMessage(getString(R.string.confirmarEliminarCarta, card.nombre))
                                .setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                                    viewModel.deleteCarta(card.idCarta, collectionId)
                                    Snackbar.make(binding.root, getString(R.string.cartaEliminada), Snackbar.LENGTH_SHORT).show()
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
        binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCards.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isGridView.collect { isGridView ->
                        adapter.setViewType(isGridView)
                        if (isGridView) {
                            binding.rvCards.layoutManager = GridLayoutManager(requireContext(), 2)
                        } else {
                            binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
                        }
                        binding.rvCards.adapter = adapter
                        requireActivity().invalidateOptionsMenu()
                    }
                }
                
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                        is CartaUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is CartaUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.updateList(state.list)
                            
                            val currentUserId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .getInt("userId", -1)
                            
                            val isOwner = state.ownerId == currentUserId
                            val isPublicView = arguments?.containsKey("userId") == true
                            
                            // Hide FAB if viewing someone else's collection
                            if (isPublicView || (state.ownerId != -1 && !isOwner)) {
                                (activity as? es.cifpcarlos3.proyecto_mesus_android.MainActivity)?.findViewById<View>(R.id.float_button)?.visibility = View.GONE
                            }
                        }
                        is CartaUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Menu already inflated by MainActivity
                // Only configure search here
            }

            override fun onPrepareMenu(menu: Menu) {
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView
                
                searchView?.queryHint = getString(R.string.buscarCartaHint)
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        
        viewModel.getCartas(collectionId)
    }

    override fun toggleView() {
        viewModel.toggleViewMode()
        requireActivity().invalidateOptionsMenu()
    }
}
