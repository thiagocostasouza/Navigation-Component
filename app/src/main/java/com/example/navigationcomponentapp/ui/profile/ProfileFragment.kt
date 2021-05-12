package com.example.navigationcomponentapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.navigationcomponentapp.R
import com.example.navigationcomponentapp.ui.login.LoginViewModel

class ProfileFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val textProfileUsername  by lazy { view?.findViewById(R.id.textProfileUsername) as TextView }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.authenticationStateEvent.observe(viewLifecycleOwner, Observer {  authenticationState ->
            when(authenticationState){
                is LoginViewModel.AuthenticationState.Authenticated -> {
                    textProfileUsername.text = getString(R.string.profile_text_username, loginViewModel.username)
                }
                is LoginViewModel.AuthenticationState.UnAuthenticated -> {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        })
    }

}