package com.parc.todo.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class TodoItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val priority: String = "medium", // low, medium, high
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
