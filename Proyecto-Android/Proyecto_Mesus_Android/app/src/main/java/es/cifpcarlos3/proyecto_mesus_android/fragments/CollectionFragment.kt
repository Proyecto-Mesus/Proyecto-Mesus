package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.adapters.CollectionAdapter
import es.cifpcarlos3.proyecto_mesus_android.databinding.CollectionFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CollectionViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ColeccionUiState

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CollectionFragment : Fragment() {
    private lateinit var binding: CollectionFragmentBinding
    private val viewModel: CollectionViewModel by viewModels()
    private lateinit var adapter: CollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString("username")
        if (username != null) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.coleccionTitular, username)
        }

        adapter = CollectionAdapter(
            emptyList(),
            onItemClick = { coleccion ->
                val bundle = Bundle().apply {
                    putInt("collectionId", coleccion.idColeccion)
                    putSerializable("coleccion", coleccion)
                    putString("username", arguments?.getString("username"))
                }
                findNavController().navigate(R.id.collectionDetailFragment, bundle)
            },
            onLongClick = { coleccion, cardView ->
                val currentUserId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE).getInt("userId", -1)
                
                if (coleccion.idUsuario == currentUserId) {
                    val popup = androidx.appcompat.widget.PopupMenu(requireContext(), cardView)
                    popup.menuInflater.inflate(R.menu.menu_context_item, popup.menu)
                    
                    popup.setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.action_edit -> {
                                val bundle = Bundle().apply {
                                    putSerializable("coleccion", coleccion)
                                }
                                findNavController().navigate(R.id.addCollectionFragment, bundle)
                                true
                            }
                            R.id.action_delete -> {
                            MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(getString(R.string.eliminarColeccion))
                                    .setMessage(getString(R.string.confirmarEliminarColeccion, coleccion.nombre))
                                    .setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                                        viewModel.deleteCollection(coleccion.idColeccion)
                                        Snackbar.make(binding.root, getString(R.string.coleccionEliminada), Snackbar.LENGTH_SHORT).show()
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

        binding.rvCollections.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollections.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ColeccionUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ColeccionUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.updateList(state.list)
                        }
                        is ColeccionUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onPrepareMenu(menu: Menu) {
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView
                
                searchView?.queryHint = getString(R.string.buscarColeccionHint)
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
    }

    override fun onResume() {
        super.onResume()
        val userId = arguments?.getInt("userId", -1) ?: -1
        viewModel.getColecciones(userId)
    }
}
