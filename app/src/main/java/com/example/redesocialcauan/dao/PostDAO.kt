package com.example.redesocialcauan.dao

import com.example.redesocialcauan.model.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class PostDAO {

    private val collection = Firebase.firestore.collection("posts")

    fun carregarFeed(
        limit: Long,
        onSuccess: (posts: List<Post>, lastVisible: DocumentSnapshot?) -> Unit,
        onError: () -> Unit
    ) {
        collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { it.toObject(Post::class.java).copy(id = it.id) }
                val last = if (result.size() > 0) result.documents[result.size() - 1] else null
                onSuccess(posts, last)
            }
            .addOnFailureListener { onError() }
    }

    fun carregarMais(
        lastVisible: DocumentSnapshot,
        limit: Long,
        onSuccess: (posts: List<Post>, lastVisible: DocumentSnapshot?) -> Unit,
        onError: () -> Unit
    ) {
        collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { it.toObject(Post::class.java).copy(id = it.id) }
                val last = if (result.size() > 0) result.documents[result.size() - 1] else null
                onSuccess(posts, last)
            }
            .addOnFailureListener { onError() }
    }

    fun buscarPorCidade(
        cidade: String,
        onSuccess: (posts: List<Post>) -> Unit,
        onError: () -> Unit
    ) {
        collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { result ->
                val posts = result
                    .map { it.toObject(Post::class.java).copy(id = it.id) }
                    .filter { it.cidade.contains(cidade, ignoreCase = true) }
                onSuccess(posts)
            }
            .addOnFailureListener { onError() }
    }

    fun excluir(
        id: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        collection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError() }
    }

    fun publicar(
        autor: String,
        texto: String,
        cidade: String,
        imagemBase64: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val dados = hashMapOf(
            "autor" to autor,
            "imagemBase64" to imagemBase64,
            "texto" to texto,
            "cidade" to cidade,
            "timestamp" to FieldValue.serverTimestamp()
        )
        collection.add(dados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError() }
    }
}
