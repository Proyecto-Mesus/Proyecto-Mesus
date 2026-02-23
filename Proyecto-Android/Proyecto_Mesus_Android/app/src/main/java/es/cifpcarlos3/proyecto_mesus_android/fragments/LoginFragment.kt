package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.databinding.LoginFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AuthViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.UsuarioUiState
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.createAccountAction)
        }

        binding.buttonLoginGoogle.setOnClickListener {
            val snackbar = Snackbar.make(binding.root, getString(R.string.errorLogin), Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(getString(R.string.cerrarSnackbar)) {
                snackbar.dismiss()
            }
            snackbar.show()
        }

        binding.user.addTextChangedListener {
            viewModel.onTextChanged(it.toString())
        }

        binding.passwd.addTextChangedListener {
            viewModel.onPasswdTextChanged(it.toString())
        }

        binding.buttonLogin.setOnClickListener {
            viewModel.performLogin()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isButtonEnabled.collect { enabled ->
                        binding.buttonLogin.isEnabled = enabled
                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is UsuarioUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.buttonLogin.isEnabled = false
                                binding.createAccount.isEnabled = false
                            }
                            is UsuarioUiState.ActionSuccess -> {
                                binding.progressBar.visibility = View.GONE
                                findNavController().navigate(R.id.eventsFragment)
                            }
                            is UsuarioUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonLogin.isEnabled = true
                                binding.createAccount.isEnabled = true
                                val message = when (state.message) {
                                    "LOGIN_FAILED" -> getString(R.string.loginIncorrecto)
                                    else -> state.message
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                            }
                            else -> {
                                binding.buttonLogin.isEnabled = true
                                binding.createAccount.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }
}
