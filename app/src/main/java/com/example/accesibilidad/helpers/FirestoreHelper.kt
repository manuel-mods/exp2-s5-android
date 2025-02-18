package com.example.accesibilidad.helpers
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    fun saveUserData(userId: String, username: String, onResult: (Boolean) -> Unit) {
        val user = hashMapOf(
            "username" to username,
            "userId" to userId
        )
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}