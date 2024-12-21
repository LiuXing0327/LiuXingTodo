package com.liuxing.todo.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liuxing.todo.R
import com.liuxing.todo.data.Todo
import com.liuxing.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Author：流星
 * DateTime：2024/12/20 11:59
 * Description：TodoBottomSheet
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InputTodoBottomSheet(
    openBottomSheet: Boolean,
    setOpenBottomSheet: (Boolean) -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope
) {
    val todoViewModel: TodoViewModel = viewModel()
    LaunchedEffect(openBottomSheet) {
        if (openBottomSheet) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { setOpenBottomSheet(false) },
            sheetState = bottomSheetState
        ) {
            var text by remember {
                mutableStateOf("")
            }
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(text = stringResource(R.string.todo)) }
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) setOpenBottomSheet(false)
                    }
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
                Button(onClick = {
                    todoViewModel.insertTodo(Todo(todoContent = text, todoFinish = false))
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) setOpenBottomSheet(false)
                    }
                }, Modifier.padding(start = 8.dp), enabled = text.isNotBlank()) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UpdateTodoBottomSheet(
    openBottomSheet: Boolean,
    setOpenBottomSheet: (Boolean) -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    todo: Todo
) {
    val todoViewModel: TodoViewModel = viewModel()
    LaunchedEffect(openBottomSheet) {
        if (openBottomSheet) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { setOpenBottomSheet(false) },
            sheetState = bottomSheetState
        ) {
            var text by remember {
                mutableStateOf(todo.todoContent)
            }
            TextField(
                value = text, onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text(text = stringResource(id = R.string.todo)) }
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    todoViewModel.deleteTodo(Todo(todo.id, text, false))
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) setOpenBottomSheet(false)
                    }
                }) {
                    Text(text = stringResource(R.string.delete))
                }
                Button(onClick = {
                    todoViewModel.updateTodo(Todo(todo.id, text, todo.todoFinish))
                    scope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) setOpenBottomSheet(false)
                    }
                }, enabled = text.isNotBlank()) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}