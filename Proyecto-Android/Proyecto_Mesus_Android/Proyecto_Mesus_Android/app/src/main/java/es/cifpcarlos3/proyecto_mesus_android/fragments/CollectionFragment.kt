package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import androidx.lifecycle.ViewModelProvider
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CollectionViewModel

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.adapters.CollectionAdapter
import es.cifpcarlos3.proyecto_mesus_android.fragments.CollectionFragmentDirections
import es.cifpcarlos3.proyecto_mesus_android.databinding.CollectionFragmentBinding

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class CollectionFragment : Fragment() {
    private var _binding: CollectionFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectionViewModel by viewModels()
    private lateinit var adapter: CollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CollectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CollectionAdapter(emptyList()) { coleccion ->
            val bundle = Bundle().apply {
                putInt("collectionId", coleccion.idColeccion)
            }
            if (findNavController().currentDestination?.id == R.id.publicCollectionFragment) {
                findNavController().navigate(R.id.action_publicCollectionFragment_to_publicCollectionDetailFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_collectionFragment_to_collectionDetailFragment, bundle)
            }
        }

        binding.rvCollections.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollections.adapter = adapter

        viewModel.colecciones.observe(viewLifecycleOwner) { lista ->
            adapter.updateData(lista)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu)
                
                // Hide view toggle as it's not applicable here
                menu.findItem(R.id.action_toggle_view)?.isVisible = false

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Buscar colección..."
                
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    override fun onResume() {
        super.onResume()
        val userId = arguments?.getInt("userId", -1) ?: -1
        viewModel.getColecciones(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
