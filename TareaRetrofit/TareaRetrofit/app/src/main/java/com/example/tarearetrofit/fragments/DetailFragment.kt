package com.example.tarearetrofit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.tarearetrofit.databinding.FragmentDetailBinding
import com.example.tarearetrofit.repository.FilmProvider
import com.example.tarearetrofit.retrofit.RetrofitInstance
import com.example.tarearetrofit.ui.FilmViewModel
import com.example.tarearetrofit.ui.FilmViewModelFactory
import com.example.tarearetrofit.ui.FilmUiState
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarearetrofit.recyclerview.FilmAdapter
import com.google.android.material.snackbar.Snackbar
import com.example.tarearetrofit.recyclerview.Character

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: FilmViewModel by lazy {
        val provider = FilmProvider(RetrofitInstance.api)
        val factory = FilmViewModelFactory(provider)
        ViewModelProvider(this, factory)[FilmViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val character =
            arguments?.getSerializable("character", Character::class.java)

        if (character != null) {
            binding.nombrePersonaje.text = character.name

            val filmAdapter = FilmAdapter(requireContext(), emptyList())
            binding.rv.layoutManager = LinearLayoutManager(requireContext())
            binding.rv.adapter = filmAdapter
            viewModel.loadFilms(character.films)
            viewLifecycleOwner.lifecycleScope.launch {

                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                    viewModel.uiState.collect { state ->
                        when (state) {
                            is FilmUiState.SuccessList -> {
                                filmAdapter.items = state.films
                                filmAdapter.notifyDataSetChanged()
                            }

                            is FilmUiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}