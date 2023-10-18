package com.ludwiglarsson.antiplanner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    fun deleteCurrentItems() {
        viewModelScope.launch {
            repository.deleteAllTodos()
        }
    }
}