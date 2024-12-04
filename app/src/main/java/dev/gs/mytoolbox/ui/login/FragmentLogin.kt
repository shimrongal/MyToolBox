package dev.gs.mytoolbox.ui.login

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dev.gs.mytoolbox.R
import dev.gs.mytoolbox.databinding.LayoutLoginScreenBinding
import dev.gs.mytoolbox.di.utils.SharedPrefManger
import dev.gs.mytoolbox.di.utils.hideKeyboard
import dev.gs.mytoolbox.ui.custom_views.ViewPassword


/**
 *   This App uses Firebase and Google auth to for authentication.
 *
 *   SignIn functionality - At first this app is currently free so I allow to users signup free so if someone one wanna check the app he does not have to any permission.
 *      *** I am using a PopUp Screen for the signup process just for demonstration the use AlertDialog with custom views.
 *
 *   *** The use of a flag to know if a user is signed on singed out is because: Firebase Authentication persists the user's session even after the app is uninstalled and reinstalled. This behavior happens because Firebase uses persistent
 *   authentication tokens that remain valid until they expire (usually after an hour) or are explicitly revoked. Firebase also caches the session in the backend, so uninstalling the app doesn't necessarily log out the user on the server.
 *       To ensure complete logout, you need to revoke the session on the Firebase server and clear any cached authentication state locally. - This option is only valid for firebase paying customers.
 *
 */

class FragmentLogin : Fragment() {

    companion object {
        private const val TAG = "SignInActivity"

        fun newInstance(): FragmentLogin {
            val args = Bundle()

            val fragment = FragmentLogin()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var layoutLoginScreenBinding: LayoutLoginScreenBinding
    private val viewModelLogin: ViewModelLogin by viewModels()

    private var isPasswordVisible: Boolean = false
    private var signUpPopUp: AlertDialog? = null

    //The use of intents should only take place in a fragment or activity
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private val signInResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                layoutLoginScreenBinding.prSignIn.visibility = View.VISIBLE
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                viewModelLogin.handleGoogleSignInResult(credential)
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException: ${e.message}")
            }
        } else {
            Log.e(TAG, "Google Sign-In failed with result code: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.i(javaClass.simpleName, "onCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(javaClass.simpleName, "onCreateView")
        layoutLoginScreenBinding = LayoutLoginScreenBinding.inflate(inflater)
        return layoutLoginScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(javaClass.simpleName, "onViewCreated")

        layoutLoginScreenBinding.apply {
            viewPassword.setOnVisibilityClick { view ->
                isPasswordVisible = !isPasswordVisible
                view.isSelected = isPasswordVisible
                // Toggle input type for password visibility
                viewPassword.setInputType(isPasswordVisible)
            }
            btnGoogleSignIn.setOnClickListener {
                oneTapClient = Identity.getSignInClient(requireContext())
                signInRequest = viewModelLogin.initSignInRequeset(getString(R.string.web_client_id))
                oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(requireActivity()) { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                        signInResultLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Error starting intent sender: ${e.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Google sign-in failed: ${e.message}")
                }
            }
            btnSignIn.setOnClickListener {
                isPasswordVisible = false
                it.hideKeyboard()
                when {
                    etEmailAddress.text.isEmpty() -> Snackbar.make(
                        layoutLoginScreenBinding.root,
                        getString(R.string.please_set_email_address),
                        Snackbar.LENGTH_LONG
                    ).show()

                    viewPassword.getPassword().isEmpty() -> Snackbar.make(
                        layoutLoginScreenBinding.root,
                        getString(R.string.please_set_email_password),
                        Snackbar.LENGTH_LONG
                    ).show()

                    else -> {
                        layoutLoginScreenBinding.prSignIn.visibility =View.VISIBLE
                        viewModelLogin.signInWithEmailAndPassword(
                            etEmailAddress.text.toString(),
                            viewPassword.getPassword().toString()
                        )
                    }
                }
            }
            btnSignUp.setOnClickListener {
                val viewSignUp = LayoutInflater.from(requireContext()).inflate(R.layout.layout_signup, null)
                val builder = AlertDialog.Builder(requireContext()).setView(viewSignUp).setTitle(getString(R.string.sign_up))
                signUpPopUp = builder.create()
                signUpPopUp!!.show()
                val btnSignUpPopUp = viewSignUp.findViewById<Button>(R.id.btnSignupNewUser)
                val etEmailAddress = viewSignUp.findViewById<EditText>(R.id.etEmailAddress)
                val etDisplayName = viewSignUp.findViewById<EditText>(R.id.etDisplayName)
                val etPhoneNumber = viewSignUp.findViewById<EditText>(R.id.etPhoneNumber)
                val etHomeAddress = viewSignUp.findViewById<EditText>(R.id.etHomeAddress)
                val vpUserPassword = viewSignUp.findViewById<ViewPassword>(R.id.vpUserPassword)
                vpUserPassword.setOnVisibilityClick { view ->
                    isPasswordVisible = !isPasswordVisible
                    view.isSelected = isPasswordVisible
                    // Toggle input type for password visibility
                    vpUserPassword.setInputType(isPasswordVisible)
                }
                btnSignUpPopUp.setOnClickListener {
                    if (etEmailAddress.text.isNotEmpty() && vpUserPassword.getPassword().isNotEmpty()) {
                        layoutLoginScreenBinding.prSignIn.visibility =View.VISIBLE
                        viewModelLogin.createNewUser(etDisplayName.text.toString(), etHomeAddress.text.toString(), etPhoneNumber.text.toString(), etEmailAddress.text.toString(), vpUserPassword.getPassword().toString())
                    } else {
                        Snackbar.make(layoutLoginScreenBinding.root, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
                        dismissSignUpPopUp()
                    }
                }
            }
        }
        viewModelLogin.currentFirebaseUser.observe(viewLifecycleOwner)
        { firebaseUser -> //TODO: save firebaseUser to db
            layoutLoginScreenBinding.prSignIn.visibility =View.GONE
            signUpPopUp?.takeIf { it.isShowing }?.let {
                dismissSignUpPopUp()
            }
            SharedPrefManger.putPreference(SharedPrefManger.IS_SIGN_IN, true)
            findNavController().navigate(R.id.action_FragmentLogin_to_FragmentToolBox)
        }
        viewModelLogin.exception.observe(viewLifecycleOwner)
        {
            Log.e(TAG, "Error: $it")
            Snackbar.make(layoutLoginScreenBinding.root, "Error: $it", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun dismissSignUpPopUp() {
        isPasswordVisible = false
        signUpPopUp!!.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_login, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_setting -> {
                Snackbar.make(layoutLoginScreenBinding.root, "TODO: please implement me", Snackbar.LENGTH_LONG).show()
                Log.w(TAG, "Not Implemented yet")
                true
            }
            R.id.nav_exit -> {
                requireActivity().finishAffinity()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}