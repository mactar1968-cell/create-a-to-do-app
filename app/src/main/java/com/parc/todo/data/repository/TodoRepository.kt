package com.parc.todo.data.repository

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.parc.todo.data.model.TodoItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TodoRepository(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    private fun todosCollection(userId: String) =
        firestore.collection("users").document(userId).collection("todos")

    fun getTodos(userId: String): Flow<List<TodoItem>> = callbackFlow {
        val subscription = todosCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TodoItem::class.java)?.copy(id = doc.id)
                    }
                    trySend(todos)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addTodo(userId: String, title: String, description: String = "", priority: String = "medium"): Result<String> {
        return try {
            val todoData = hashMapOf(
                "title" to title,
                "description" to description,
                "completed" to false,
                "priority" to priority,
                "createdAt" to Timestamp.now()
            )
            val docRef = todosCollection(userId).add(todoData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleTodo(userId: String, todoId: String, currentStatus: Boolean): Result<Unit> {
        return try {
            todosCollection(userId).document(todoId)
                .update(
                    mapOf(
                        "completed" to !currentStatus,
                        "updatedAt" to Timestamp.now()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTodo(userId: String, todoId: String): Result<Unit> {
        return try {
            todosCollection(userId).document(todoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
