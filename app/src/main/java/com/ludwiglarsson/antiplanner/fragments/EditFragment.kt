package com.ludwiglarsson.antiplanner.fragments

/**
 * UI класс, который отвечает за редактирование элемента в списке.
 */
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.ludwiglarsson.antiplanner.viewmodels.EditViewModel
import com.ludwiglarsson.antiplanner.viewmodels.appViewModels
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.icu.util.Calendar
import android.util.Log
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.viewmodels.EditViewModel.State.Loading
import com.ludwiglarsson.antiplanner.viewmodels.EditViewModel.State.Success
import com.ludwiglarsson.antiplanner.viewmodels.EditViewModel.Actions
import com.ludwiglarsson.antiplanner.App
import com.ludwiglarsson.antiplanner.todos.TodoItem
import kotlinx.coroutines.launch
import java.util.Date

class EditFragment : SoloTodoFragment() {

    private val viewModel by appViewModels<EditViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appComponent = (requireContext().applicationContext as App).appComponent
        appComponent.getEditFragmentComponentFactory().create()
        if (savedInstanceState == null) {
            Log.d("arguments", requireArguments().toString())
            val todoID = requireArguments().getString(ARGUMENT_KEY) as String
            viewModel.init(todoID)
        }
        setUpCollects(view.context, view)
        setUpUI()
    }

    private fun setUpCollects(context: Context, view: View) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.states.collect { state ->
                        when (state) {
                            is Success -> showSuccessState(state)
                            is Loading -> showLoadingState()
                        }
                    }
                }
                launch {
                    viewModel.actions.collect { action ->
                        when (action) {
                            Actions.Exit -> parentFragmentManager.popBackStackImmediate()
                            is Actions.Error -> showErrorAction(action, view)
                            Actions.CalendarPicker -> showCalendarPicker(context)
                            is Actions.SetText -> setInitialText(action.text)
                        }
                    }
                }
            }
        }
    }

    private fun setUpUI() {
        binding.delImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        binding.delButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        binding.delButton.isEnabled = true

        binding.delButton.setOnClickListener {
            viewModel.onDeleteTodo()
        }
        binding.saveButton.setOnClickListener {
            viewModel.onSaveClick(binding.todoEdit.text.toString())
        }
        binding.switchButton.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) viewModel.onCheckedChanged(isChecked)
        }
        binding.deadlineDate.setOnClickListener {
            viewModel.onDeadlineClick()
        }
        binding.changePriority.setOnClickListener {
            val popupMenu = PopupMenu(it.context, binding.changePriority)
            popupMenu.menuInflater.inflate(R.menu.priority_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                val priority = when (item.itemId) {
                    R.id.action_low -> TodoItem.Priority.LOW
                    R.id.action_normal -> TodoItem.Priority.NORMAL
                    R.id.action_high -> TodoItem.Priority.HIGH
                    else -> error("Unexpected ID")
                }
                viewModel.onPriorityChanged(priority)
                true
            }
            popupMenu.show()
        }
    }

    private fun showLoadingState() {
        binding.progressAddEdit.visibility = View.VISIBLE
    }

    private fun showErrorAction(state: Actions.Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.dark_green)).show()
    }

    private fun showSuccessState(state: Success) {
        binding.progressAddEdit.visibility = View.GONE

        internalSetDeadline(state.item.deadline)
        internalSetPriority(state.item.itemPriority)
    }

    private fun setInitialText(text: String) {
        binding.todoEdit.setText(text)
    }

    private fun showCalendarPicker(context: Context) {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, mYear, mMonth, mDay ->
                viewModel.onDeadlineChanged(Date(mYear - DATE_YEAR_OFFSET, mMonth, mDay))
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, _ ->
            viewModel.onCalendarCancel()
        }
        datePickerDialog.show()
    }

    companion object {
        private const val ARGUMENT_KEY = "TodoID"

        fun createNewInstance(id: String): EditFragment {
            val bundle = Bundle()
            val fragmentEdit = EditFragment()
            bundle.putString(ARGUMENT_KEY, id)
            fragmentEdit.arguments = bundle
            return fragmentEdit
        }
    }
}