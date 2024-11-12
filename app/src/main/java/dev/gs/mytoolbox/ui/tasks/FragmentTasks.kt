package dev.gs.mytoolbox.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.gs.mytoolbox.databinding.LayoutTasksScreenBinding

class FragmentTasks : Fragment() {

    private lateinit var layoutTasksScreenBinding: LayoutTasksScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutTasksScreenBinding = LayoutTasksScreenBinding.inflate(inflater)
        return layoutTasksScreenBinding.root
    }
}