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
import es.cifpcarlos3.proyecto_mesus_android.databinding.LoginFragmentBinding
import es.cifpcarlos3.proyecto_mesus_android.viewmodels.AuthViewModel

class LoginFragment: Fragment() {
    private lateinit var binding: LoginFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.createAccountAction)
        }

        binding.buttonLoginGoogle.setOnClickListener {
            val snackbar = Snackbar.make(view, getString(R.string.errorLogin), Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(getString(R.string.cerrarSnackbar)) {
                snackbar.dismiss()
            }
            snackbar.show()
        }

        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.user.addTextChangedListener{
            viewModel.onTextChanged(it.toString())
        }

        binding.passwd.addTextChangedListener{
            viewModel.onPasswdTextChanged(it.toString())
        }

        viewModel.isButtonEnabled.observe(viewLifecycleOwner, Observer<Boolean> { value ->
            binding.buttonLogin.isEnabled = value
        })

        viewModel.loginResult.observe(viewLifecycleOwner, Observer<Boolean> { success ->
            if (success) {
                val snackbar = Snackbar.make(view, getString(R.string.loginCorrecto), Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                val snackbar = Snackbar.make(view, getString(R.string.loginIncorrecto), Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        })

        binding.buttonLogin.setOnClickListener {
           viewModel.performLogin()
        }
    }
}
