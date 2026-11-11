package com.parc.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.parc.todo.data.model.TodoItem
import com.parc.todo.data.repository.AuthRepository
import com.parc.todo.data.repository.TodoRepository
import com.parc.todo.ui.screens.AuthScreen
import com.parc.todo.ui.screens.TodoListScreen
import kotlinx.coroutines.flow.flowOf

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository()
        val todoRepository = TodoRepository()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser by authRepository.authStateFlow.collectAsState(
                        initial = authRepository.currentUser
                    )

                    val activeUser = currentUser
                    if (activeUser == null) {
                        AuthScreen(
                            onSignIn = { email, password ->
                                authRepository.signInWithEmail(email, password)
                            },
                            onSignUp = { email, password ->
                                authRepository.signUpWithEmail(email, password)
                            }
                        )
                    } else {
                        val todosFlow = remember(activeUser.uid) {
                            todoRepository.getTodos(activeUser.uid)
                        }
                        val todos by todosFlow.collectAsState(initial = emptyList())

                        TodoListScreen(
                            user = activeUser,
                            todos = todos,
                            onAddTodo = { title, desc, priority ->
                                todoRepository.addTodo(activeUser.uid, title, desc, priority)
                            },
                            onToggleTodo = { todoId, currentStatus ->
                                todoRepository.toggleTodo(activeUser.uid, todoId, currentStatus)
                            },
                            onDeleteTodo = { todoId ->
                                todoRepository.deleteTodo(activeUser.uid, todoId)
                            },
                            onSignOut = {
                                authRepository.signOut()
                            }
                        )
                    }
                }
            }
        }
    }
}
