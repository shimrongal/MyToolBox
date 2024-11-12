package dev.gs.mytoolbox.ui.login

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.gs.mytoolbox.R
import dev.gs.mytoolbox.databinding.LayoutLoginScreenBinding
import dev.gs.mytoolbox.di.utils.SharedPrefManger
import dev.gs.mytoolbox.di.utils.SharedPrefManger.IS_USER_LOGGED_IN
import dev.gs.mytoolbox.di.utils.hideKeyboard
import dev.gs.mytoolbox.ui.mytoolbox.FragmentMyToolBox

class FragmentLogin : Fragment() {

    companion object {
        fun newInstance(): FragmentLogin {
            val args = Bundle()

            val fragment = FragmentLogin()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var layoutLoginScreenBinding: LayoutLoginScreenBinding
    private val currentClassName: String = javaClass.name
    private val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var signInRequest: BeginSignInRequest
    private val oneTapClient by lazy {
        Identity.getSignInClient(requireContext())
    }

    private val signInResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                Log.i(currentClassName, "Received Google ID Token")

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign-in success
                                Log.d(currentClassName, "signInWithCredential:success")
                                val user = firebaseAuth.currentUser
                                Log.i(currentClassName, "User authenticated: $user")

                                SharedPrefManger.putPreference(IS_USER_LOGGED_IN, true)
                                // Navigate to the next screen
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.layout_navigation_drawer_activity,
                                        FragmentMyToolBox.newInstance(),
                                        "FragmentToolBoxMain"
                                    )
                                    .commit()
                            } else {
                                // Sign-in failed
                                Log.w(
                                    currentClassName,
                                    "signInWithCredential:failure",
                                    task.exception
                                )
                                //Update app data on fail login
                                SharedPrefManger.putPreference(IS_USER_LOGGED_IN, false)
                            }
                        }
                } else {
                    Log.d(currentClassName, "No Google ID token received")
                }
            } catch (e: ApiException) {
                Log.e(currentClassName, "ApiException: ${e.message}")
            }
        } else {
            Log.e(
                currentClassName,
                "Google Sign-In failed with result code: ${result.resultCode}"
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutLoginScreenBinding = LayoutLoginScreenBinding.inflate(inflater)
        return layoutLoginScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutLoginScreenBinding.apply {
            btnGoogleSignIn.setOnClickListener {
                signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))  // Correct way to access the string resource
                        .setFilterByAuthorizedAccounts(true).build()
                ).build()

                // Trigger Google One Tap sign-in
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        try {
                            val intentSenderRequest = IntentSenderRequest
                                .Builder(result.pendingIntent.intentSender)
                                .build()
                            signInResultLauncher.launch(intentSenderRequest)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(currentClassName, "Error starting intent sender: ${e.message}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(currentClassName, "Google sign-in failed: ${e.message}")
                    }
            }
            btnSignIn.setOnClickListener {
                it.hideKeyboard()
                when {
                    etEmailAddress.text.isEmpty() -> Snackbar.make(
                        root,
                        "Please set Email address",
                        Snackbar.LENGTH_LONG
                    ).show()

                    etPassword.text.isEmpty() -> Snackbar.make(
                        root,
                        "Please set Email password",
                        Snackbar.LENGTH_LONG
                    ).show()

                    else -> {
                        firebaseAuth.signInWithEmailAndPassword(
                            etEmailAddress.text.toString(),
                            etPassword.text.toString()
                        ).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //TODO: go to main
                                SharedPrefManger.putPreference(IS_USER_LOGGED_IN, true)
                                Log.i(currentClassName, "SignIn Success")
                                try {
                                    findNavController().navigate(R.id.action_FragmentLogin_to_FragmentTasks)
                                } catch (e: IllegalStateException) {
                                    Log.e(currentClassName, "IllegalStateException: ${e.message}")
                                }
                            } else {
                                Log.i(currentClassName, "Current user is NOT registered")
                                Toast.makeText(
                                    context,
                                    "Current user is not registered, please register.",
                                    Toast.LENGTH_LONG
                                ).show()
                                SharedPrefManger.putPreference(IS_USER_LOGGED_IN, false)
                            }
                        }.addOnFailureListener { task ->
                            Snackbar.make(root, "${task.message}", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
            btnSignUp.setOnClickListener {
                //TODO: Create sign in page
                Snackbar.make(root, "Sign up page is not ready yet", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(
                        Color.White.value.toInt()
                    ).setTextColor(Color.White.value.toInt()).show()
            }
            btnCancelSignIn.setOnClickListener {
                //TODO:  REMOVE THE LOGGED IN MOCK
                SharedPrefManger.putPreference(IS_USER_LOGGED_IN, false)
                requireActivity().finish()
            }
        }
    }

}