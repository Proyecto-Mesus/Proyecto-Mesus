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
import es.cifpcarlos3.proyecto_mesus_android.databinding.RegisterFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.NewUserViewModel
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.UsuarioUiState
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var binding: RegisterFragmentBinding
    private val viewModel: NewUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerUser.addTextChangedListener {
            viewModel.onTextChanged(it.toString())
        }

        binding.registerPasswd.addTextChangedListener {
            viewModel.onPasswdTextChanged(it.toString())
        }

        binding.confirmPsswd.addTextChangedListener {
            viewModel.onConfirmPasswdTextChanged(it.toString())
        }
        binding.registerEmail.addTextChangedListener {
            viewModel.onEmailTextChanged(it.toString())
        }

        binding.registerButton.setOnClickListener {
            val name = binding.registerUser.text.toString()
            val email = binding.registerEmail.text.toString()
            val pass = binding.registerPasswd.text.toString()
            val confirmPass = binding.confirmPsswd.text.toString()

            if (pass != confirmPass) {
                Snackbar.make(binding.root, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.performRegister()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isButtonEnabled.collect { enabled ->
                        binding.registerButton.isEnabled = enabled
                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is UsuarioUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.registerButton.isEnabled = false
                            }
                            is UsuarioUiState.ActionSuccess -> {
                                binding.progressBar.visibility = View.GONE
                                binding.registerButton.isEnabled = true
                                Snackbar.make(binding.root, getString(R.string.registroExitoso), Snackbar.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            is UsuarioUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.registerButton.isEnabled = true
                                val message = when(state.message) {
                                    "USER_EXISTS" -> getString(R.string.usuarioExiste)
                                    "EMAIL_EXISTS" -> getString(R.string.emailExiste)
                                    "PASSWORDS_MISMATCH" -> "Las contraseñas no coinciden"
                                    else -> state.message
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                                when (state.message) {
                                    "PASSWORDS_MISMATCH" -> {
                                        val error = getString(R.string.contraseñasIncorrectas)
                                        binding.registerPasswd.error = error
                                        binding.confirmPsswd.error = error
                                    }
                                    "USER_EXISTS" -> {
                                        binding.registerUser.error = getString(R.string.usuarioExiste)
                                    }
                                    "EMAIL_EXISTS" -> {
                                        binding.registerEmail.error = getString(R.string.emailExiste)
                                    }
                                    else -> {
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
