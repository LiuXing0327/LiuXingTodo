package com.liuxing.todo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Author：流星
 * DateTime：2024/12/19 10:59
 * Description：TodoDao
 */
@Dao
interface TodoDao {

    @Insert
    suspend fun insertTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("SELECT * FROM TODO ORDER BY ID DESC")
    fun queryAllTodo(): LiveData<List<Todo>>

    @Query("SELECT * FROM TODO WHERE TODO_Content LIKE '&' || :query || '&'")
    suspend fun queryFilterTodo(query: String): List<Todo>
}