package es.cifpcarlos3.proyecto_mesus_android.fragments

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
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddCollectionFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddCollectionViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ColeccionUiState
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.JuegoUiState

import android.widget.ArrayAdapter
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego
import kotlinx.coroutines.launch

class AddCollectionFragment : Fragment() {
    private lateinit var binding: AddCollectionFragmentBinding
    private val viewModel: AddCollectionViewModel by viewModels()
    private var gameList: List<Juego> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddCollectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchJuegos()

        val collectionToEdit = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("coleccion", es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("coleccion") as? es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
        }
        
        if (collectionToEdit != null) {
            binding.etCollectionName.setText(collectionToEdit.nombre)
            binding.btnSaveCollection.text = getString(R.string.actualizar)
            binding.switchPublic.isChecked = collectionToEdit.publica == 1
        }

        binding.btnSaveCollection.setOnClickListener {
            val name = binding.etCollectionName.text.toString()
            val selectedGamePosition = binding.spinnerGames.selectedItemPosition
            if (selectedGamePosition >= 0 && selectedGamePosition < gameList.size) {
                val gameId = gameList[selectedGamePosition].idJuego
                val isPublic = binding.switchPublic.isChecked
                
                if (collectionToEdit != null) {
                    viewModel.actualizarColeccion(collectionToEdit.idColeccion, name, gameId, isPublic)
                } else {
                    viewModel.guardarColeccion(name, gameId, isPublic)
                }
            } else {
                Snackbar.make(binding.root, getString(R.string.seleccionarJuego), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.juegosState.collect { state ->
                        when (state) {
                            is JuegoUiState.Loading -> {
                            }
                            is JuegoUiState.Success -> {
                                gameList = state.list
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, state.list.map { it.nombre })
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spinnerGames.adapter = adapter
                                
                                collectionToEdit?.let { col ->
                                    val gamePos = state.list.indexOfFirst { it.idJuego == col.idJuego }
                                    if (gamePos != -1) {
                                        binding.spinnerGames.setSelection(gamePos)
                                    }
                                }
                            }
                            is JuegoUiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
                
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is ColeccionUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.btnSaveCollection.isEnabled = false
                            }
                            is ColeccionUiState.ActionSuccess -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnSaveCollection.isEnabled = true
                                Snackbar.make(binding.root, getString(R.string.coleccionCreada), Snackbar.LENGTH_LONG).show()
                                findNavController().popBackStack()
                            }
                            is ColeccionUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnSaveCollection.isEnabled = true
                                val message = when(state.message) {
                                    "NOMBRE_VACIO" -> getString(R.string.errorNombreVacio)
                                    "SESSION_ERROR" -> getString(R.string.errorSesion)
                                    "GUARDAR_ERROR" -> getString(R.string.errorGuardado)
                                    "ACTUALIZAR_ERROR" -> getString(R.string.errorActualizacion)
                                    else -> state.message
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {
                                binding.btnSaveCollection.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }
}
