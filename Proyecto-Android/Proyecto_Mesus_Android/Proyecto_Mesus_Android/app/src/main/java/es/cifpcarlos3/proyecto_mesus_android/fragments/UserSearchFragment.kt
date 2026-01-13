package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.proyecto_mesus_android.databinding.SearchUsersFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.UserSearchViewModel
import es.cifpcarlos3.proyecto_mesus_android.adapters.UserAdapter

class UserSearchFragment : Fragment() {

    private var _binding: SearchUsersFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchUsersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(emptyList()) { user ->
            val action = UserSearchFragmentDirections.actionUserSearchFragmentToPublicCollectionFragment(user.idUsuario)
            findNavController().navigate(action)
        }

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
            binding.tvNoResults.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    viewModel.searchUsers(s.toString())
                } else {
                    adapter.updateList(emptyList())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
