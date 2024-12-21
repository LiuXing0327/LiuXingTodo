package com.liuxing.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author：流星
 * DateTime：2024/12/19 10:40
 * Description：ToDoEntity
 */
@Entity(tableName = "TODO")
data class Todo(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "TODO_Content")
    val todoContent: String,

    @ColumnInfo(name = "TODO_FINISH")
    val todoFinish: Boolean
)