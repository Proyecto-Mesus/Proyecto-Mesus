package com.example.tarearetrofit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarearetrofit.databinding.FragmentListBinding
import com.example.tarearetrofit.recyclerview.*
import com.example.tarearetrofit.retrofit.RetrofitInstance
import com.example.tarearetrofit.ui.CharacterViewModel
import kotlinx.coroutines.launch
import com.example.tarearetrofit.ui.*
import com.example.tarearetrofit.repository.*
import com.example.tarearetrofit.R
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private val viewModel: CharacterViewModel by lazy {
        val provider = CharacterProvider(RetrofitInstance.api)
        val factory = CharacterViewModelFactory(provider)
        ViewModelProvider(this, factory)[CharacterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->
                    when (state) {

                        CharacterUiState.Idle -> Unit

                        CharacterUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rv.visibility = View.GONE

                            binding.rv.layoutManager = LinearLayoutManager(requireContext())
                        }

                        is CharacterUiState.Success -> {

                            val characters = listOf(state.character)
                            val adapter = CharacterAdapter(requireContext(), characters)

                            adapter.setOnItemClickListener(object: CharacterAdapter.OnItemClickListener{
                                override fun onItemClick(position: Int) {
                                    val character = characters[position]
                                    val bundle = Bundle()
                                    bundle.putSerializable("character", character)
                                    findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
                                }
                            })

                            binding.rv.adapter = adapter

                            binding.progressBar.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE

                        }
                        is CharacterUiState.SuccessList -> {

                            val lista = state.characters
                            val adapter = CharacterAdapter(requireContext(), lista)


                            adapter.setOnItemClickListener(object: CharacterAdapter.OnItemClickListener{
                                override fun onItemClick(position: Int) {
                                    val character = lista[position]
                                    val bundle = Bundle()
                                    bundle.putSerializable("character", character)
                                    findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
                                }
                            })
                            binding.rv.adapter = adapter

                            binding.progressBar.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE


                        }

                        is CharacterUiState.Error -> {

                            binding.progressBar.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE

                            Snackbar.make(
                                binding.root,
                                state.message,
                                Snackbar.LENGTH_LONG
                            ).show()

                        }
                    }
                }
            }

        }//Coroutine


        viewModel.loadCharacter(1)
        binding.button.setOnClickListener {
            viewModel.loadCharacters(binding.textInputEditText.text.toString())
        }

    }
}

