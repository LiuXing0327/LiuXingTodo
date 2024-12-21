package com.liuxing.todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.dynamicColorScheme
import com.liuxing.todo.data.Todo
import com.liuxing.todo.ui.components.AboutAlertDialog
import com.liuxing.todo.ui.search.SearchBar
import com.liuxing.todo.ui.theme.ToDoTheme
import com.liuxing.todo.ui.todo.InputTodoBottomSheet
import com.liuxing.todo.ui.todo.TodoItem
import com.liuxing.todo.ui.todo.UpdateTodoBottomSheet
import com.liuxing.todo.utils.CheckUpdateDialog
import com.liuxing.todo.viewmodel.TodoViewModel


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ToDoTheme {
                CheckUpdateDialog()
                dynamicColorScheme(this)

                val todoViewModel: TodoViewModel = viewModel()

                val filteredTodos by todoViewModel.queryFilteredTodos.observeAsState(emptyList())

                var openBottomSheet by remember { mutableStateOf(false) }
                var selectedTodo by remember { mutableStateOf<Todo?>(null) }

                val skipPartiallyExpanded by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                val bottomSheetState =
                    rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

                if (openBottomSheet && selectedTodo != null) {
                    UpdateTodoBottomSheet(
                        openBottomSheet = openBottomSheet,
                        setOpenBottomSheet = { openBottomSheet = it },
                        bottomSheetState = bottomSheetState,
                        scope = scope,
                        todo = selectedTodo!!
                    )
                }

                Scaffold(
                    topBar = {
                        TopAppBar()
                    },
                    floatingActionButton = {
                        FloatingActionButton()
                    }
                ) { paddingValues ->

                    val completedTasks = filteredTodos.filter { it.todoFinish }
                    val pendingTasks = filteredTodos.filter { !it.todoFinish }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding()
                            ),
                    ) {
                        item {
                            Box(Modifier.padding(8.dp)) {
                                Text("待处理")
                            }
                        }

                        items(pendingTasks) { todo ->
                            TodoItem(
                                todo = todo,
                                onclick = {
                                    selectedTodo = todo
                                    openBottomSheet = true
                                },
                                starOnClick = {
                                    todoViewModel.updateTodo(todo.copy(todoFinish = !todo.todoFinish))
                                }
                            )
                        }

                        item {
                            Box(Modifier.padding(8.dp)) {
                                Text("已完成")
                            }
                        }

                        items(completedTasks) { todo ->
                            TodoItem(
                                todo = todo,
                                onclick = {
                                    selectedTodo = todo
                                    openBottomSheet = true
                                },
                                starOnClick = {
                                    todoViewModel.updateTodo(todo.copy(todoFinish = !todo.todoFinish))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ToDoTheme {
            Scaffold(
                topBar = {
                    TopAppBar()
                },
                //  floatingActionButton = { FloatingActionButton() }
            ) {
                Text(modifier = Modifier.padding(it), text = "Preview")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBar() {
        var query by remember { mutableStateOf("") }
        var isMenuExpanded by remember { mutableStateOf(false) }
        var isDialogVisible by remember {
            mutableStateOf(false)
        }
        val todoViewModel: TodoViewModel = viewModel()
        val interactionSource = remember { MutableInteractionSource() }
        TopAppBar(
            title = {

            },
            actions = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = query,
                        onQueryChange = { newQuery ->
                            query = newQuery
                            todoViewModel.queryFilterTodo(newQuery)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    IconButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .clip(CircleShape)
                            .indication(
                                interactionSource,
                                LocalIndication.current
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }) {
                        DropdownMenuItem(text = {
                            Text(text = "关于")
                        }, onClick = {
                            isDialogVisible = true
                            isMenuExpanded = false
                        })
                    }
                    AboutAlertDialog(
                        showDialog = isDialogVisible,
                        { isDialogVisible = false },
                        context = this@MainActivity
                    )
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FloatingActionButton() {
        var openBottomSheet by rememberSaveable { mutableStateOf(false) }
        val skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val bottomSheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

        Column {
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = { openBottomSheet = !openBottomSheet },
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_mode_24),
                            contentDescription = "Add"
                        )
                    },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                )
            }
            InputTodoBottomSheet(openBottomSheet, { openBottomSheet = it }, bottomSheetState, scope)
        }
    }
}