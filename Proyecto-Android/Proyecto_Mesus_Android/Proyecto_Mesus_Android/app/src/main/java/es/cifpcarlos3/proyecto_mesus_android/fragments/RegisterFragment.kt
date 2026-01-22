package es.cifpcarlos3.proyecto_mesus_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.proyecto_mesus_android.R
import es.cifpcarlos3.proyecto_mesus_android.databinding.RegisterFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.NewUserViewModel

class RegisterFragment: Fragment() {
    private lateinit var binding: RegisterFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this)[NewUserViewModel::class.java]

        viewModel.isButtonEnabled.observe(viewLifecycleOwner, Observer<Boolean> { value ->
            binding.registerButton.isEnabled = value
        })

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

        viewModel.registerError.observe(viewLifecycleOwner, Observer<String?> { error ->
            if (error == null) {
                if (binding.registerUser.text?.isNotEmpty() == true) {
                    val snackbar = Snackbar.make(view, "Usuario registrado correctamente", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    findNavController().navigate(R.id.loginFragment)
                }
            } else {
                if (error.contains("contraseñas")) {
                    binding.registerPasswd.error = error
                    binding.confirmPsswd.error = error
                }else if(error.contains("nombre")){
                    binding.registerUser.error = error
                }else if(error.contains("correo")){
                    binding.registerEmail.error = error
                }
            }
        })

        binding.registerButton.setOnClickListener {
            viewModel.performRegister()
        }
    }
}
