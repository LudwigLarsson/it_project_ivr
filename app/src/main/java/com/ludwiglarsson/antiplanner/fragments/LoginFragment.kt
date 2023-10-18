package com.ludwiglarsson.antiplanner.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.antiplanner.databinding.FragmentLoginBinding

import com.ludwiglarsson.antiplanner.App
import com.ludwiglarsson.antiplanner.utils.SharedPreferencesHelper
import com.ludwiglarsson.antiplanner.viewmodels.LoginViewModel
import com.ludwiglarsson.antiplanner.viewmodels.appViewModels
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthSdk
import javax.inject.Inject


class LoginFragment : Fragment() {


    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var sdk : YandexAuthSdk

    private val viewModel by appViewModels<LoginViewModel>()

    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(layoutInflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireContext().applicationContext as App).appComponent.getLoginFragmentComponentFactory().create().inject(this)

        val register: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == -1) {
                val data: Intent? = result.data
                if (data != null) {
                    try {
                        val yandexAuthToken = sdk.extractToken(result.resultCode, data)
                        if (yandexAuthToken != null) {
                            val curToken = yandexAuthToken.value
                            if (curToken != sharedPreferencesHelper.getToken()) {
                                sharedPreferencesHelper.putToken(curToken)
                                sharedPreferencesHelper.putRevision(0)
                                viewModel.deleteCurrentItems()
                            }
                            //moveToTasks()
                        }
                    } catch (exception: YandexAuthException) {
                        Toast.makeText(context, exception.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        views {
            loginWithYandexButton.setOnClickListener {
                register.launch(sdk.createLoginIntent(YandexAuthLoginOptions.Builder().build()))
            }
            loginButton.setOnClickListener {
                if(sharedPreferencesHelper.getToken() != "unaffordable") {
                    sharedPreferencesHelper.putToken("unaffordable")
                    sharedPreferencesHelper.putRevision(0)
                    viewModel.deleteCurrentItems()
                }
                //moveToTasks()
            }
        }
    }

    /*private fun moveToTasks() {
        val action = LoginFragmentDirections.actionMainTasks()
        findNavController().navigate(action)
    }*/

    private fun <T : Any> views(block: FragmentLoginBinding.() -> T): T? = binding?.block()


}