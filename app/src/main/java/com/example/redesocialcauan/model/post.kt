package com.example.redesocialcauan.model


data class Post(
    val id: String = "",
    val autor: String = "",
    val cidade: String = "",
    val texto: String = "",
    val imagemBase64: String = "",
    @com.google.firebase.firestore.ServerTimestamp
    var timestamp: java.util.Date? = null
)
