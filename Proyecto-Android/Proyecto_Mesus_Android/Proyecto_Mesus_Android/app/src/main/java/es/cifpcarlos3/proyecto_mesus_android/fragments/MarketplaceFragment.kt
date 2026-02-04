package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.databinding.MarketplaceFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MarketplaceViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.MercadoUiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MarketplaceFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: MarketplaceFragmentBinding
    private val viewModel: MarketplaceViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MarketplaceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.marketplace_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeViewModel()
        viewModel.fetchItems()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MercadoUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is MercadoUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            googleMap?.clear()
                            state.list.forEach { item ->
                                val pos = LatLng(item.latitud, item.longitud)
                                val cardInfo = if (item.nombreCarta != null) " • Carta: ${item.nombreCarta}" else ""
                                googleMap?.addMarker(
                                    MarkerOptions().position(pos)
                                        .title(item.nombre)
                                        .snippet("${item.precio}€$cardInfo")
                                )
                            }
                            if (state.list.isNotEmpty()) {
                                val firstItem = LatLng(state.list[0].latitud, state.list[0].longitud)
                                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstItem, 10f))
                            }
                        }
                        is MercadoUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        
        val currentState = viewModel.uiState.value
        if (currentState is MercadoUiState.Success) {
            googleMap?.clear()
            currentState.list.forEach { item ->
                val pos = LatLng(item.latitud, item.longitud)
                val cardInfo = if (item.nombreCarta != null) " • Carta: ${item.nombreCarta}" else ""
                googleMap?.addMarker(
                    MarkerOptions().position(pos)
                        .title(item.nombre)
                        .snippet("${item.precio}€$cardInfo")
                )
            }
        }
    }
}
