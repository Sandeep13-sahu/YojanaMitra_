package com.example.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException

enum class OperationType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LIST
}

private const val TAG = "FirestoreError"

fun handleFirestoreError(e: Exception, operation: OperationType, path: String) {
    val firestoreEx = generateSequence(e as Throwable) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()

    val code = firestoreEx?.code?.name ?: "UNKNOWN"
    val message = firestoreEx?.message ?: e.message ?: "Unknown Firestore error"

    Log.e(
        TAG,
        """{"event":"firestore_error","operation":"${operation.name}","path":"$path","code":"$code","message":"$message"}"""
    )
}
