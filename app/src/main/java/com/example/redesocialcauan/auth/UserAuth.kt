package com.example.redesocialcauan.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class UserAuth {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun estaLogado(): Boolean = firebaseAuth.currentUser != null

    fun emailUsuarioAtual(): String? = firebaseAuth.currentUser?.email

    fun login(
        email: String,
        senha: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val msg = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuário não cadastrado."
                        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos."
                        else -> "Erro ao fazer login: ${task.exception?.message}"
                    }
                    onError(msg)
                }
            }
    }

    fun cadastrar(
        email: String,
        senha: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val msg = when (task.exception) {
                        is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso."
                        is FirebaseAuthWeakPasswordException -> "A senha precisa ter pelo menos 6 caracteres."
                        else -> "Erro ao cadastrar: ${task.exception?.message}"
                    }
                    onError(msg)
                }
            }
    }

    fun logoff() = firebaseAuth.signOut()

    fun alterarSenha(
        novaSenha: String,
        onResult: (Boolean) -> Unit
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onResult(false)
            return
        }
        user.updatePassword(novaSenha)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }
}
