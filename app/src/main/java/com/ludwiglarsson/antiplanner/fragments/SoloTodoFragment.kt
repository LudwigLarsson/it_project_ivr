package com.ludwiglarsson.antiplanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.antiplanner.R
import com.example.antiplanner.databinding.ActivityMainBinding
import com.example.antiplanner.databinding.FragmentNewBinding
import com.ludwiglarsson.antiplanner.todos.TodoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class SoloTodoFragment : Fragment(R.layout.fragment_new) {

    lateinit var binding: FragmentNewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    protected fun internalSetDeadline(deadline: Date?) {
        if (deadline == null) {
            binding.deadlineDate.text = ""
            binding.switchButton.isChecked = false
        } else {
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU"))
            val mDate = formatter.format(deadline)
            binding.deadlineDate.text = mDate
            binding.switchButton.isChecked = true
        }
    }

    protected fun internalSetPriority(priority: TodoItem.Priority) {
        when (priority) {
            TodoItem.Priority.LOW -> {
                binding.changePriority.setText(R.string.low_priority)
                binding.changePriority.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.contrast)
                )
            }

            TodoItem.Priority.NORMAL -> {
                binding.changePriority.setText(R.string.normal_priority)
                binding.changePriority.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.contrast)
                )
            }

            TodoItem.Priority.HIGH -> {
                binding.changePriority.setText(R.string.high_priority)
                binding.changePriority.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }
    }
    companion object {
        const val DATE_YEAR_OFFSET = 1900
    }
}