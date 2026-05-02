package com.example.redesocialcauan.dao

import com.example.redesocialcauan.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class UserDAO {

    private val collection = Firebase.firestore.collection("usuarios")

    fun salvar(
        user: User,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val dados = hashMapOf(
            "nomeCompleto" to user.nomeCompleto,
            "username" to user.username,
            "fotoPerfil" to user.fotoPerfil
        )
        collection.document(user.email).set(dados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError() }
    }

    fun atualizar(
        email: String,
        dados: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        collection.document(email).set(dados, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError() }
    }

    fun carregar(
        email: String,
        onResult: (User?) -> Unit
    ) {
        collection.document(email).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = User(
                        email = email,
                        nomeCompleto = document.getString("nomeCompleto") ?: "",
                        username = document.getString("username") ?: "",
                        fotoPerfil = document.getString("fotoPerfil") ?: ""
                    )
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }
}
