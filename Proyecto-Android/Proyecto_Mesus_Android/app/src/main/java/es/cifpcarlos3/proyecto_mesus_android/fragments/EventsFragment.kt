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
import es.cifpcarlos3.proyecto_mesus_android.databinding.EventsFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventsViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventoUiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EventsFragment : Fragment(), OnMapReadyCallback, ViewTogglable {
    private lateinit var binding: EventsFragmentBinding
    private val viewModel: EventsViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun isListView(): Boolean = viewModel.isListMode.value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EventsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isListMode.collect { isListMode ->
                        if (isListMode) {
                            binding.mapContainer.visibility = View.GONE
                            binding.listContainer.visibility = View.VISIBLE

                            if (childFragmentManager.findFragmentByTag("events_tabs") == null) {
                                childFragmentManager.beginTransaction()
                                    .replace(R.id.listContainer, EventsTabFragment(), "events_tabs")
                                    .commit()
                            }
                        } else {
                            binding.mapContainer.visibility = View.VISIBLE
                            binding.listContainer.visibility = View.GONE
                        }
                        requireActivity().invalidateOptionsMenu()
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is EventoUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is EventoUiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                googleMap?.clear()
                                state.list.forEach { evento ->
                                    val pos = LatLng(evento.latitud, evento.longitud)
                                    googleMap?.addMarker(
                                        MarkerOptions().position(pos).title(evento.nombre)
                                    )
                                }
                                if (state.list.isNotEmpty()) {
                                    val firstEvent = LatLng(state.list[0].latitud, state.list[0].longitud)
                                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstEvent, 10f))
                                }
                            }
                            is EventoUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
        viewModel.fetchEvents()
    }


    override fun toggleView() {
        viewModel.toggleViewMode()
        requireActivity().invalidateOptionsMenu()
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        
        // Ubicación por defecto (España)
        val defaultLoc = LatLng(40.416775, -3.703790)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 13f))
        
        val currentState = viewModel.uiState.value
        if (currentState is EventoUiState.Success) {
            googleMap?.clear()
            currentState.list.forEach { event ->
                val position = LatLng(event.latitud, event.longitud)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(event.nombre)
                        .snippet(event.descripcion)
                )
            }
            if (currentState.list.isNotEmpty()) {
                val firstEvent = LatLng(currentState.list[0].latitud, currentState.list[0].longitud)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstEvent, 10f))
            }
        }
    }
}
