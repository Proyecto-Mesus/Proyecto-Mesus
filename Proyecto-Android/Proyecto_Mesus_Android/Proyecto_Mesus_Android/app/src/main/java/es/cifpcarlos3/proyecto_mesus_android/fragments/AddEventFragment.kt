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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddEventFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddEventViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventoUiState
import kotlinx.coroutines.launch

class AddEventFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: AddEventFragmentBinding
    private val viewModel: AddEventViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEventFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.add_event_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSaveEvent.setOnClickListener {
            val name = binding.etEventName.text.toString()
            val desc = binding.etEventDescription.text.toString()
            val date = binding.etEventDate.text.toString()
            
            if (selectedLocation == null) {
                Snackbar.make(binding.root, "Por favor, selecciona una ubicación en el mapa", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPrefs = requireContext().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("userId", -1)

            viewModel.saveEvent(name, desc, date, selectedLocation!!.latitude, selectedLocation!!.longitude, userId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is EventoUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnSaveEvent.isEnabled = false
                        }
                        is EventoUiState.ActionSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveEvent.isEnabled = true
                            Snackbar.make(binding.root, getString(R.string.eventoGuardado), Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        is EventoUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSaveEvent.isEnabled = true
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            binding.btnSaveEvent.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // Ubicación por defecto (España por ejemplo)
        val defaultLoc = LatLng(40.416775, -3.703790)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 5f))

        googleMap?.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
        }
    }
}
