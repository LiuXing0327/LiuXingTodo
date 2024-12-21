package com.liuxing.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.liuxing.todo.data.Todo
import com.liuxing.todo.repository.TodoRepository
import kotlinx.coroutines.launch

/**
 * Author：流星
 * DateTime：2024/12/19 11:37
 * Description：TodoViewModel
 */
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val todoRepository: TodoRepository = TodoRepository(application)
    private val _queryFilteredTodos = MutableLiveData<List<Todo>>(emptyList())
    val queryFilteredTodos: LiveData<List<Todo>> get() = _queryFilteredTodos

    private val allTodos: LiveData<List<Todo>> = todoRepository.queryAllTodo()

    private var currentQuery = ""

    init {
        allTodos.observeForever { _ ->
            queryFilterTodo(currentQuery)
        }
    }

    fun insertTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
        }
    }

    fun queryAllTodo(): LiveData<List<Todo>> {
        return allTodos
    }

    fun queryFilterTodo(query: String) {
        currentQuery = query

        val currentTodos = allTodos.value ?: return
        _queryFilteredTodos.value = if (query.isEmpty()) {
            currentTodos
        } else {
            currentTodos.filter {
                it.todoContent.contains(query, ignoreCase = true)
            }
        }
    }
}
