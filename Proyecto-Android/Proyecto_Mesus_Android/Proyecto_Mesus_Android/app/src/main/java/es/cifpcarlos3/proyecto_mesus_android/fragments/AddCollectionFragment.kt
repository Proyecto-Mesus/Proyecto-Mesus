package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddCollectionFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddCollectionViewModel

import android.widget.ArrayAdapter
import es.cifpcarlos3.proyecto_mesus_android.data.models.Juego

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

        binding.btnSaveCollection.setOnClickListener {
            val name = binding.etCollectionName.text.toString()
            val selectedGamePosition = binding.spinnerGames.selectedItemPosition
            if (selectedGamePosition >= 0 && selectedGamePosition < gameList.size) {
                val gameId = gameList[selectedGamePosition].idJuego
                val isPublic = binding.switchPublic.isChecked
                viewModel.guardarColeccion(name, gameId, isPublic)
            } else {
                Snackbar.make(view, "Por favor, selecciona un juego", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.juegos.observe(viewLifecycleOwner) { juegos ->
            gameList = juegos
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, juegos.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGames.adapter = adapter
        }

        viewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            binding.btnSaveCollection.isEnabled = !isSaving
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (it == "SUCCESS") {
                    Snackbar.make(view, "Colección creada", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
