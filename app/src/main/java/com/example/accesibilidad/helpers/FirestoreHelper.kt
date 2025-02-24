package com.example.accesibilidad.helpers

import com.example.accesibilidad.screens.SavedText
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

    fun saveText(userId: String, text: String, onResult: (Boolean, String) -> Unit) {
        val data = hashMapOf(
            "userId" to userId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("savedTexts").add(data)
            .addOnSuccessListener { documentRef ->
                onResult(true, documentRef.id)
            }
            .addOnFailureListener {
                onResult(false, "")
            }
    }


    fun getSavedTexts(userId: String, onResult: (List<SavedText>) -> Unit) {
        db.collection("savedTexts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val texts = snapshot.documents.mapNotNull { doc ->
                    val text = doc.getString("text")
                    if (text != null) SavedText(doc.id, text) else null
                }
                onResult(texts)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun deleteText(textId: String) {
        db.collection("savedTexts").document(textId).delete()
    }
}
