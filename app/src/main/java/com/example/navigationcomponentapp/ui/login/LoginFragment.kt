package com.example.navigationcomponentapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.navigationcomponentapp.R
import com.example.navigationcomponentapp.extensions.dismissError
import com.example.navigationcomponentapp.extensions.navigateWithAnimation
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private val buttonSignIn by lazy { view?.findViewById(R.id.buttonLoginSignIn) as Button }
    private val buttonSignUp  by lazy { view?.findViewById(R.id.buttonLoginSignUp) as Button }
    private val inputLoginUserName by lazy { view?.findViewById(R.id.inputLoginUsername) as EditText }
    private val inputLoginPassword by lazy { view?.findViewById(R.id.inputLoginPassword) as EditText }
    private val inputLayoutLoginUserName by lazy { view?.findViewById(R.id.inputLayoutLoginUsername) as TextInputLayout }
    private val inputLayoutPassword by lazy { view?.findViewById(R.id.inputLayoutLoginPassword) as TextInputLayout }
    private val viewModel: LoginViewModel by activityViewModels()
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val validationFields = initValidationFields()
        listenToAuthenticationStateEvent(validationFields)
        registerViewListeners()
        registerDeviceBackStackCallback()
    }

    private fun initValidationFields() = mapOf(
        LoginViewModel.INPUT_USERNAME.first to inputLayoutLoginUserName,
        LoginViewModel.INPUT_PASSWORD.first to inputLayoutPassword
    )

    private fun listenToAuthenticationStateEvent(validationFields: Map<String, TextInputLayout>) {
        viewModel.authenticationStateEvent.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                is LoginViewModel.AuthenticationState.Authenticated -> {
                    navController.popBackStack()
                }
                is LoginViewModel.AuthenticationState.InvalidAuthentication -> {
                    authenticationState.fields.forEach { fieldError ->
                        validationFields[fieldError.first]?.error = getString(fieldError.second)
                    }
                }
            }
        })
    }

    private fun registerViewListeners() {
        buttonSignIn.setOnClickListener {
            val username = inputLoginUserName.text.toString()
            val password = inputLoginPassword.text.toString()

            viewModel.authentication(username, password)
        }

        buttonSignUp.setOnClickListener {
            navController.navigateWithAnimation(R.id.action_loginFragment_to_navigation)
        }

        inputLoginUserName.addTextChangedListener {
            inputLayoutLoginUserName.dismissError()
        }

        inputLoginPassword.addTextChangedListener {
            inputLayoutPassword.dismissError()
        }
    }

    private fun registerDeviceBackStackCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cancelAuthentication()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        cancelAuthentication()
        return true
    }

    private fun cancelAuthentication() {
        viewModel.refuseAuthentication()
        navController.popBackStack(R.id.startFragment, false)
    }
}