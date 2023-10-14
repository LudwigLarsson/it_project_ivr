package com.ludwiglarsson.antiplanner.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.todos.TodoItem
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result.Success
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result.Failure
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class EditViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    private val mutableStates = MutableStateFlow<State>(State.Loading)
    private val mutableActions = MutableSharedFlow<Actions>(replay = 0)
    val states: StateFlow<State> = mutableStates
    val actions: SharedFlow<Actions> = mutableActions

    fun init(todoId: String) {
        viewModelScope.launch {
            mutableStates.value = State.Loading
            val result = repository.getTodo(todoId)
            when (result) {
                is Failure -> {
                    mutableActions.emit(Actions.Error(R.string.get_error))
                    mutableActions.emit(Actions.Exit)
                }

                is Success -> {
                    mutableActions.emit(Actions.SetText(result.value.itemText))
                    mutableStates.value = State.Success(result.value)
                }
            }
        }
    }

    fun onSaveClick(text: String) {
        val state: State.Success = getSuccessState() ?: return
        val dateOfChanges = Calendar.getInstance().time
        val updatedItem = state.item.copy(dateOfChanges = dateOfChanges, itemText = text)
        viewModelScope.launch {
            mutableStates.value = State.Loading
            val result = repository.updateTodo(updatedItem)
            when (result) {
                is Failure -> {
                    mutableStates.value = State.Success(updatedItem)
                    mutableActions.emit(Actions.Error(R.string.save_error))
                }

                is Success -> mutableActions.emit(Actions.Exit)
            }
        }
    }

    fun onDeleteTodo() {
        val item = getSuccessState()?.item ?: return
        viewModelScope.launch {
            mutableStates.value = State.Loading
            val result = repository.deleteTodo(item.itemID)
            when (result) {
                is Failure -> {
                    mutableStates.value = State.Success(item)
                    mutableActions.emit(Actions.Error(R.string.delete_error))
                }

                is Success -> mutableActions.emit(Actions.Exit)
            }
        }
    }

    fun onDeadlineChanged(date: Date) {
        val state: State.Success = getSuccessState() ?: return
        val updatedItem = state.item.copy(deadline = date)
        mutableStates.value = State.Success(updatedItem)
    }

    fun onPriorityChanged(priority: TodoItem.Priority) {
        val state: State.Success = getSuccessState() ?: return
        val updatedItem = state.item.copy(itemPriority = priority)
        mutableStates.value = State.Success(updatedItem)
    }

    fun onCheckedChanged(isChecked: Boolean) {
        val state: State.Success = getSuccessState() ?: return
        if (!isChecked) {
            val updatedItem = state.item.copy(deadline = null)
            mutableStates.value = State.Success(updatedItem)
        } else {
            val updatedItem = state.item.copy(deadline = Calendar.getInstance().time)
            mutableStates.value = State.Success(updatedItem)
            viewModelScope.launch { mutableActions.emit(Actions.CalendarPicker) }
        }
    }

    fun onCalendarCancel() {
        val state: State.Success = getSuccessState() ?: return
        val updatedItem = state.item.copy(deadline = null)
        mutableStates.value = State.Success(updatedItem)
    }

    fun onDeadlineClick() {
        viewModelScope.launch { mutableActions.emit(Actions.CalendarPicker) }
    }

    private fun getSuccessState(): State.Success? {
        return states.value as? State.Success
    }

    sealed interface State {
        data class Success(val item: TodoItem) : State

        object Loading : State
    }

    sealed interface Actions {
        object Exit : Actions

        object CalendarPicker : Actions

        class SetText(val text: String) : Actions

        class Error(@StringRes val messageID: Int) : Actions
    }
}