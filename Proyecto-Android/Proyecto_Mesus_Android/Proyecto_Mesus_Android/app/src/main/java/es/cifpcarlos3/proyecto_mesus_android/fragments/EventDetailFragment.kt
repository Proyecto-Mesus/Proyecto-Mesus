package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.data.models.Evento
import es.cifpcarlos3.proyecto_mesus_android.databinding.FragmentEventDetailBinding

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventDetailViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.EventDetailUiState
import kotlinx.coroutines.launch

class EventDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentEventDetailBinding
    private val viewModel: EventDetailViewModel by viewModels()
    private var evento: Evento? = null
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        evento = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("evento", Evento::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("evento") as? Evento
        }

        evento?.let { currentEvento ->
            binding.tvDetailEventName.text = currentEvento.nombre
            binding.tvDetailEventDate.text = currentEvento.fecha
            binding.tvDetailEventDescription.text = currentEvento.descripcion
            
            binding.btnOpenChat.setOnClickListener {
                viewModel.openOrCreateChat(currentEvento.idEvento)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is EventDetailUiState.Loading -> {
                            binding.progressBarDetail.visibility = View.VISIBLE
                        }
                        is EventDetailUiState.ChatReady -> {
                            binding.progressBarDetail.visibility = View.GONE
                            viewModel.resetState()
                            val bundle = Bundle().apply {
                                putSerializable("conversacion", state.conversacion)
                            }
                            findNavController().navigate(R.id.chatDetailFragment, bundle)
                        }
                        is EventDetailUiState.Error -> {
                            binding.progressBarDetail.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            binding.progressBarDetail.visibility = View.GONE
                        }
                    }
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        binding.progressBarDetail.visibility = View.GONE
        evento?.let {
            val location = LatLng(it.latitud, it.longitud)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(it.nombre)
            )
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
        }
    }
}
