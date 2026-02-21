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
import android.os.Build
import es.cifpcarlos3.proyecto_mesus_android.adapters.MensajeAdapter
import es.cifpcarlos3.proyecto_mesus_android.data.models.Conversacion
import es.cifpcarlos3.proyecto_mesus_android.databinding.ChatDetailFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ChatUiState
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

class ChatDetailFragment : Fragment() {
    private lateinit var binding: ChatDetailFragmentBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MensajeAdapter
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        currentUserId = sharedPrefs.getInt("userId", -1)

        adapter = MensajeAdapter(emptyList(), currentUserId)
        binding.rvMensajes.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ChatUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ChatUiState.SuccessMensajes -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.updateData(state.list)
                            if (state.list.isNotEmpty()) {
                                binding.rvMensajes.scrollToPosition(state.list.size - 1)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
        
        val conversacion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("conversacion", Conversacion::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("conversacion") as? Conversacion
        }

        conversacion?.let { conv ->
            activity?.title = conv.otroUsuarioNombre
            viewModel.getMensajes(conv.id)

            binding.btnEnviar.setOnClickListener {
                val texto = binding.etMensaje.text.toString()
                if (texto.isNotBlank()) {
                    viewModel.enviarMensaje(conv.id, texto)
                    binding.etMensaje.text.clear()
                }
            }
        }
    }
}
