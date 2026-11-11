package com.parc.todo.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
