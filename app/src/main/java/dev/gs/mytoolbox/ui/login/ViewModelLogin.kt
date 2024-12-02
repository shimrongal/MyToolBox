package dev.gs.mytoolbox.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class ViewModelLogin(app: Application) : AndroidViewModel(app) {

    private val _currentFirebaseUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val currentFirebaseUser: LiveData<FirebaseUser> get() = _currentFirebaseUser

    private val _exception: MutableLiveData<Exception> = MutableLiveData()
    val exception: LiveData<Exception> get() = _exception


    fun handleGoogleSignInResult(credential: SignInCredential) {
        val idToken = credential.googleIdToken
        Log.i(javaClass.simpleName, "Received Google ID Token")

        if (idToken != null) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in success
                    Log.d(javaClass.simpleName, "signInWithCredential:success")
                    FirebaseAuth.getInstance().currentUser?.let {
                        _currentFirebaseUser.postValue(it)
                    } ?: Log.e(javaClass.simpleName, "Exception : Firebase user is null")


                } else {
                    // Sign-in failed
                    Log.w(javaClass.simpleName, "signInWithCredential:failure", task.exception)
                    _exception.postValue(task.exception)
                }
            }
        } else {
            Log.d(javaClass.simpleName, "No Google ID token received")
            _exception.postValue(Exception("No Google ID token received"))
        }
    }

    fun initSignInRequeset(webClientId: String): BeginSignInRequest {
        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(webClientId)
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false).build()
        ).build()
    }

    fun signInWithEmailAndPassword(emailAddress: String, emailPass: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailAddress, emailPass).addOnCompleteListener { task ->
            task.result.user?.let {
                Log.i(javaClass.simpleName, "SignIn Success")
                _currentFirebaseUser.postValue(it)
            } ?: _exception.postValue(task.exception)
        }
    }

    fun createNewUser(displayName: String = "", homeAddress: String = "", phoneNumber: String = "", emailAddress: String, emailPass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailAddress, emailPass).addOnCompleteListener { task ->
            task.result.user?.let { user ->
                val userProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
                user.updateProfile(userProfileChangeRequest).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(javaClass.simpleName, "DisplayName updated")
                    } else {
                        Log.w(javaClass.simpleName, "Error: DisplayName NOT updated")
                    }
                }
                _currentFirebaseUser.postValue(user)
            } ?: _exception.postValue(task.exception)
        }
    }

}


