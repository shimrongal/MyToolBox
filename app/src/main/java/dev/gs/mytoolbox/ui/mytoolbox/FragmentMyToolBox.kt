package dev.gs.mytoolbox.ui.mytoolbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.gs.mytoolbox.databinding.LayoutMainToolboxScreenBinding
import dev.gs.mytoolbox.di.utils.SharedPrefManger
import dev.gs.mytoolbox.di.utils.SharedPrefManger.IS_USER_LOGGED_IN


class FragmentMyToolBox : Fragment() {
    companion object {
        fun newInstance(): FragmentMyToolBox {
            val args = Bundle()

            val fragment = FragmentMyToolBox()
            fragment.arguments = args
            return fragment
        }
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val currentClass: String = javaClass.simpleName
    private lateinit var layoutMainToolboxScreenBinding: LayoutMainToolboxScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutMainToolboxScreenBinding = LayoutMainToolboxScreenBinding.inflate(inflater)
        return layoutMainToolboxScreenBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutMainToolboxScreenBinding.apply {
            btnBabyApp.setOnClickListener {
                //TODO: Go To Baby app feature screen
                Log.i(currentClass, "btnBabyApp")
            }
            btnTasksApp.setOnClickListener {
                //TODO: Go to Tasks app feature screen
                Log.i(currentClass, "btnTasksApp DEBUG ")
            }
            btnExit.setOnClickListener {
                logoutAndExit()
            }
        }
    }

    private fun logoutAndExit() {
        SharedPrefManger.putPreference(IS_USER_LOGGED_IN, false)
        firebaseAuth.signOut()
        requireActivity().finish()
    }
}