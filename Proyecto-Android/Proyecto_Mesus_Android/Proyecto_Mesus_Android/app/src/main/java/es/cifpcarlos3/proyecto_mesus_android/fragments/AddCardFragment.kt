package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.databinding.AddCardFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.data.utils.CloudinaryHelper
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AddCardViewModel

class AddCardFragment : Fragment() {
    private lateinit var binding: AddCardFragmentBinding
    private val viewModel: AddCardViewModel by viewModels()
    private val args: AddCardFragmentArgs by navArgs()
    
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

        binding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSaveCard.setOnClickListener {
            val name = binding.etCardName.text.toString()
            val set = binding.etCardSet.text.toString()
            val number = binding.etCardNumber.text.toString()
            viewModel.guardarCarta(name, set, number, selectedImageUri, args.collectionId)
        }

        viewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            binding.btnSaveCard.isEnabled = !isSaving
            binding.btnSelectImage.isEnabled = !isSaving
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (it == "SUCCESS") {
                    Snackbar.make(view, "Carta guardada con éxito", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
