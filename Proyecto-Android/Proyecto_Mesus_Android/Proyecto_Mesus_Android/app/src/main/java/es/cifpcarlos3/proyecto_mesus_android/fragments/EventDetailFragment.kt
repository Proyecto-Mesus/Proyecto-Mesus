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

class EventDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentEventDetailBinding
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

        evento?.let {
            binding.tvDetailEventName.text = it.nombre
            binding.tvDetailEventDate.text = it.fecha
            binding.tvDetailEventDescription.text = it.descripcion
        }
        binding.progressBarDetail.visibility = View.VISIBLE
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
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}
