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
import es.cifpcarlos3.proyecto_mesus_android.adapters.ChatAdapter
import es.cifpcarlos3.proyecto_mesus_android.databinding.ChatFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ChatUiState
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.ChatViewModel
import es.cifpcarlos3.proyecto_mesus_android.R
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private lateinit var binding: ChatFragmentBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = ChatAdapter(emptyList()) { conversacion ->
            val bundle = Bundle().apply {
                putSerializable("conversacion", conversacion)
            }
            findNavController().navigate(R.id.action_chatListFragment_to_chatDetailFragment, bundle)
        }
        binding.rvConversaciones.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ChatUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyChat.visibility = View.GONE
                        }
                        is ChatUiState.SuccessList -> {
                            binding.progressBar.visibility = View.GONE
                            if (state.list.isEmpty()) {
                                binding.tvEmptyChat.visibility = View.VISIBLE
                                adapter.updateData(emptyList())
                            } else {
                                binding.tvEmptyChat.visibility = View.GONE
                                adapter.updateData(state.list)
                            }
                        }
                        is ChatUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmptyChat.apply {
                                visibility = View.VISIBLE
                                text = state.message
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        viewModel.getConversaciones()
    }
}
