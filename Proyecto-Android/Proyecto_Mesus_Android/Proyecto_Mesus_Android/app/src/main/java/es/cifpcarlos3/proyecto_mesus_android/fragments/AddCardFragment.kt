package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.data.models.Coleccion
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddCardFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.data.utils.CloudinaryHelper
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddCardViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CartaUiState
import kotlinx.coroutines.launch

class AddCardFragment : Fragment() {
    private lateinit var binding: AddCardFragmentBinding
    private val viewModel: AddCardViewModel by viewModels()
    private var currentCollection: Coleccion? = null
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.cardImagePreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        CloudinaryHelper.init(requireContext())
//        val collectionId = arguments?.getInt("collectionId") ?: -1

        currentCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("coleccion", Coleccion::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("coleccion") as? Coleccion
        }

        val cardToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("carta", Carta::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("carta") as? Carta
        }
        
        if (cardToEdit != null) {
            binding.etCardName.setText(cardToEdit.nombre)
            binding.etCardSet.setText(cardToEdit.set)
            binding.etCardNumber.setText(cardToEdit.numeroSet)
            binding.btnSaveCard.text = getString(R.string.actualizar)
            cardToEdit.imagen?.let {
                Glide.with(this).load(it).into(binding.cardImagePreview)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSaveCard.setOnClickListener {
            val name = binding.etCardName.text.toString()
            val set = binding.etCardSet.text.toString()
            val number = binding.etCardNumber.text.toString()
            
            if (currentCollection == null) {
                Snackbar.make(binding.root, "Error: Colección no encontrada", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (cardToEdit != null) {
                viewModel.actualizarCarta(cardToEdit.idCarta, name, set, number, selectedImageUri, currentCollection!!, cardToEdit.imagen)
            } else {
                viewModel.guardarCarta(name, set, number, selectedImageUri, currentCollection!!)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is CartaUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnSaveCard.isEnabled = false
                            binding.btnSelectImage.isEnabled = false
                            Snackbar.make(binding.root, getString(R.string.subiendoImagen), Snackbar.LENGTH_SHORT).show()
                        }
                        is CartaUiState.ActionSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveCard.isEnabled = true
                            binding.btnSelectImage.isEnabled = true
                            Snackbar.make(binding.root, getString(R.string.cartaGuardada), Snackbar.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                        is CartaUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveCard.isEnabled = true
                            binding.btnSelectImage.isEnabled = true
                            val message = when(state.message) {
                                "NOMBRE_VACIO" -> getString(R.string.errorNombreVacio)
                                "SUBIR_IMAGEN_ERROR" -> getString(R.string.errorSubirImagen)
                                "GUARDAR_DB_ERROR" -> getString(R.string.errorGuardarDB)
                                else -> state.message
                            }
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.btnSaveCard.isEnabled = true
                            binding.btnSelectImage.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}
