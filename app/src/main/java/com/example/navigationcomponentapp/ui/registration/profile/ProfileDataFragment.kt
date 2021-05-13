package com.example.navigationcomponentapp.ui.registration.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.navigationcomponentapp.R
import com.example.navigationcomponentapp.ui.registration.RegistrationViewModel
import com.google.android.material.textfield.TextInputLayout


class ProfileDataFragment : Fragment() {

    private val buttonProfileDataNext by lazy { view?.findViewById(R.id.buttonProfileDataNext) as Button }
    private val inputLayoutProfileDataName by lazy { view?.findViewById(R.id.inputLayoutProfileDataName) as TextInputLayout }
    private val inputLayoutProfileDataBio by lazy { view?.findViewById(R.id.inputLayoutProfileDataBio) as TextInputLayout }
    private val inputProfileDataName by lazy { view?.findViewById(R.id.inputProfileDataName) as EditText }
    private val inputProfileDataBio by lazy { view?.findViewById(R.id.inputProfileDataBio) as EditText }
    private val registrationViewModel: RegistrationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val validationFields = initValidationFields()
        listenToRegistrationViewModelEvents(validationFields)

       val name = inputProfileDataName.text.toString()
        val profile = inputProfileDataBio.text.toString()

        buttonProfileDataNext.setOnClickListener {
            registrationViewModel.collectProfileData(name, profile)
        }
    }
   private fun initValidationFields() = mapOf(
        RegistrationViewModel.INPUT_NAME.first to inputLayoutProfileDataName,
        RegistrationViewModel.INPUT_BIO.first to inputLayoutProfileDataBio
    )
  private  fun listenToRegistrationViewModelEvents(validationFields: Map<String, TextInputLayout>){
        registrationViewModel.registrationStateEvent.observe(viewLifecycleOwner, Observer { registrationState ->
            when(registrationState) {
                is RegistrationViewModel.RegistrationState.CollectCredentials -> {
                    val name = inputProfileDataName.text.toString()
                    val directions = ProfileDataFragmentDirections
                        .actionProfileDataFragmentToChooseCredentialsFragment(name)

                    findNavController().navigate(directions)
                }
                is RegistrationViewModel.RegistrationState.InvalidProfileData -> {
                    registrationState.fields.forEach { fieldError ->
                        validationFields[fieldError.first]?.error = getString(fieldError.second)
                    }
                }
            }
        })
    }
}