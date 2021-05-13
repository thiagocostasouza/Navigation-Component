package com.example.navigationcomponentapp.ui.registration.choosecredentials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.navigationcomponentapp.R
import com.example.navigationcomponentapp.extensions.dismissError
import com.example.navigationcomponentapp.ui.login.LoginViewModel
import com.example.navigationcomponentapp.ui.registration.RegistrationViewModel
import com.google.android.material.textfield.TextInputLayout

class ChooseCredentialsFragment : Fragment() {
    private val buttonChooseCredentialsNext by lazy { view?.findViewById(R.id.buttonChooseCredentialsNext) as Button }
    private val inputLayoutChooseCredentialsUsername by lazy { view?.findViewById(R.id.inputLayoutChooseCredentialsUsername) as TextInputLayout }
    private val inputLayoutChooseCredentialsPassword by lazy { view?.findViewById(R.id.inputLayoutChooseCredentialsPassword) as TextInputLayout }
    private val inputChooseCredentialsUsername by lazy { view?.findViewById(R.id.inputChooseCredentialsUsername) as EditText }
    private val inputChooseCredentialsPassword by lazy { view?.findViewById(R.id.inputChooseCredentialsPassword) as EditText }
    private val textChooseCredentialsName by lazy { view?.findViewById(R.id.textChooseCredentialsName) as TextView }
    private val args: ChooseCredentialsFragmentArgs by navArgs()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val registrationViewModel: RegistrationViewModel by activityViewModels()


    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        textChooseCredentialsName.text = getString(R.string.choose_credentials_text_name, args.name)

        val invalidFields = initValidationFields()
        listenToRegistrationStateEvent(invalidFields)
        registerViewListeners()
        registerDeviceBackStack()
    }

    private fun initValidationFields() = mapOf(
        RegistrationViewModel.INPUT_USERNAME.first to inputLayoutChooseCredentialsUsername,
        RegistrationViewModel.INPUT_PASSWORD.first to inputLayoutChooseCredentialsPassword
    )

    private fun listenToRegistrationStateEvent(validationFields: Map<String, TextInputLayout>) {
        registrationViewModel.registrationStateEvent.observe(viewLifecycleOwner, Observer { registrationState ->
            when (registrationState) {
                is RegistrationViewModel.RegistrationState.RegistrationCompleted -> {
                    val token = registrationViewModel.authToken
                    val username = inputChooseCredentialsUsername.text.toString()

                    loginViewModel.authenticateToken(token, username)
                    navController.popBackStack(R.id.profileFragment, false)
                }
                is RegistrationViewModel.RegistrationState.InvalidCredentials -> {
                    registrationState.fields.forEach { fieldError ->
                        validationFields[fieldError.first]?.error = getString(fieldError.second)
                    }
                }
            }
        })
    }

    private fun registerViewListeners() {
        buttonChooseCredentialsNext.setOnClickListener {
            val username = inputChooseCredentialsUsername.text.toString()
            val password = inputChooseCredentialsPassword.text.toString()

            registrationViewModel.createCredentials(username, password)
        }

        inputChooseCredentialsUsername.addTextChangedListener {
            inputLayoutChooseCredentialsUsername.dismissError()
        }

        inputChooseCredentialsPassword.addTextChangedListener {
            inputLayoutChooseCredentialsPassword.dismissError()
        }
    }

    private fun registerDeviceBackStack() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            cancelRegistration()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        cancelRegistration()
        return super.onOptionsItemSelected(item)
    }

    private fun cancelRegistration() {
        registrationViewModel.userCancelledRegistration()
        navController.popBackStack(R.id.loginFragment, false)
    }
}