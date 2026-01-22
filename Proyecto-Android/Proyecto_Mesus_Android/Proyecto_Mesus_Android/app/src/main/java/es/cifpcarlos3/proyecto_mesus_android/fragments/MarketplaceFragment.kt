package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.proyecto_mesus_android.R

import es.cifpcarlos3.proyecto_mesus_android.databinding.MarketplaceFragmentBinding

class MarketplaceFragment : Fragment() {
    private lateinit var binding: MarketplaceFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MarketplaceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    }
}
