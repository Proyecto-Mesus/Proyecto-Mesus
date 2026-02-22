package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
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
import com.google.android.material.textfield.TextInputLayout
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddEventFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddEventViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventoUiState
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEventFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: AddEventFragmentBinding
    private val viewModel: AddEventViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var selectedLocation: LatLng? = null

    private var eventoEditar: Evento? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEventFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventoEditar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("evento", Evento::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("evento") as? Evento
        }

        eventoEditar?.let { evento ->
            binding.etEventName.setText(evento.nombre)
            binding.etEventDescription.setText(evento.descripcion)
            binding.etEventDate.setText(evento.fecha)
            binding.btnSaveEvent.text = getString(R.string.actualizarEvento)
            selectedLocation = LatLng(evento.latitud, evento.longitud)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.add_event_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.etEventDate.setOnClickListener { showDateTimePicker() }
        binding.etEventDate.parent.parent.let { layout ->
            (layout as? TextInputLayout)
                ?.setEndIconOnClickListener { showDateTimePicker() }
        }

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

            val evento = eventoEditar
            if (evento != null) {
                viewModel.updateEvent(evento.idEvento, name, desc, date, selectedLocation!!.latitude, selectedLocation!!.longitude, userId)
            } else {
                viewModel.saveEvent(name, desc, date, selectedLocation!!.latitude, selectedLocation!!.longitude, userId)
            }
        }
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
                            val msg = if (eventoEditar != null) getString(R.string.eventoActualizado) else getString(R.string.eventoGuardado)
                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
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

        val defaultLoc = selectedLocation ?: LatLng(40.416775, -3.703790)
        val zoom = if (selectedLocation != null) 13f else 5f
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, zoom))

        selectedLocation?.let { loc ->
            googleMap?.addMarker(MarkerOptions().position(loc).title("Ubicación actual"))
        }

        googleMap?.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
        }
    }

    private fun showDateTimePicker() {
        val cal = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        val fechaHora = String.format(
                            "%02d/%02d/%04d %02d:%02d",
                            day,
                            month + 1,
                            year,
                            hour,
                            minute
                        )
                        binding.etEventDate.setText(fechaHora)
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
