package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.databinding.SearchUsersFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.UserSearchViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.UsuarioUiState
import es.cifpcarlos3.proyecto_mesus_android.adapters.UserAdapter
import es.cifpcarlos3.proyecto_mesus_android.R
import kotlinx.coroutines.launch

class UserSearchFragment : Fragment() {

    private lateinit var binding: SearchUsersFragmentBinding
    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchUsersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(emptyList()) { user ->
            val bundle = Bundle().apply {
                putInt("userId", user.idUsuario)
                putString("username", user.nombre)
            }
            findNavController().navigate(R.id.collectionFragment, bundle)
        }

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UsuarioUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is UsuarioUiState.SuccessList -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.updateList(state.list)
                            binding.tvNoResults.visibility = if (state.list.isEmpty()) View.VISIBLE else View.GONE
                        }
                        is UsuarioUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        else -> {}
                    }
                }
            }
        }

        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
