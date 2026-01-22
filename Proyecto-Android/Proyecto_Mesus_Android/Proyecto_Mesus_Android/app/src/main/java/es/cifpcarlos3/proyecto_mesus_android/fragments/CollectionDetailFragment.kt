package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.adapters.CardAdapter
import es.cifpcarlos3.proyecto_mesus_android.databinding.CollectionDetailFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CollectionDetailViewModel

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager

class CollectionDetailFragment : Fragment() {
    private lateinit var binding: CollectionDetailFragmentBinding
    private val viewModel: CollectionDetailViewModel by viewModels()
    private val args: CollectionDetailFragmentArgs by navArgs()
    private lateinit var adapter: CardAdapter
    private var isGridView = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = CardAdapter(emptyList())
        binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCards.adapter = adapter

        viewModel.cartas.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu)
                
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Buscar carta..."
                
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_toggle_view -> {
                        isGridView = !isGridView
                        adapter.setViewType(isGridView)
                        
                        if (isGridView) {
                            binding.rvCards.layoutManager = GridLayoutManager(requireContext(), 2)
                        } else {
                            binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
                        }
                        
                        binding.rvCards.adapter = adapter
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        viewModel.getCartas(args.collectionId)
    }

    }
}
