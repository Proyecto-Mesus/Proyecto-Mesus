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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.R
import android.widget.ArrayAdapter
import es.cifpcarlos3.proyecto_mesus_android.data.models.Carta
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.CartaUiState
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddMarketplaceItemFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddMarketplaceItemViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MercadoUiState
import kotlinx.coroutines.launch

class AddMarketplaceItemFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: AddMarketplaceItemFragmentBinding
    private val viewModel: AddMarketplaceItemViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var selectedLocation: LatLng? = null
    private var userCards: List<Carta> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMarketplaceItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.add_item_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val sharedPrefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val currentUserId = sharedPrefs.getInt("userId", -1)

        if (currentUserId != -1) {
            viewModel.fetchUserCards(currentUserId)
        }

        binding.btnSaveItem.setOnClickListener {
            val name = binding.etItemName.text.toString()
            val desc = binding.etItemDescription.text.toString()
            val priceStr = binding.etItemPrice.text.toString()
            val price = priceStr.toDoubleOrNull() ?: 0.0
            
            val selectedCardPos = binding.spinnerCards.selectedItemPosition
            val idCarta = if (selectedCardPos > 0) userCards[selectedCardPos - 1].idCarta else null

            if (selectedLocation == null) {
                Snackbar.make(binding.root, "Selecciona una ubicación de entrega", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentUserId == -1) {
                Snackbar.make(binding.root, "Error: Sesión no válida", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveItem(name, desc, price, selectedLocation!!.latitude, selectedLocation!!.longitude, currentUserId, idCarta)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is MercadoUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.btnSaveItem.isEnabled = false
                            }
                            is MercadoUiState.ActionSuccess -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnSaveItem.isEnabled = true
                                Snackbar.make(binding.root, getString(R.string.itemGuardado), Snackbar.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            is MercadoUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnSaveItem.isEnabled = true
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                            else -> {
                                binding.btnSaveItem.isEnabled = true
                            }
                        }
                    }
                }
                
                launch {
                    viewModel.cartasState.collect { state ->
                        if (state is CartaUiState.Success) {
                            userCards = state.list
                            val cardNames = mutableListOf("Ninguna (Solo venta directa)")
                            cardNames.addAll(state.list.map { it.nombre })
                            
                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cardNames)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerCards.adapter = adapter
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        val defaultLoc = LatLng(40.416775, -3.703790)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 5f))

        googleMap?.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Punto de entrega"))
        }
    }
}
