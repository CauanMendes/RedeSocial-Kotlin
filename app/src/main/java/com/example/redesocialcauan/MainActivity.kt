package com.example.redesocialcauan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocialcauan.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFireBase()
        setupListener()
    }

    fun setupFireBase(){
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val telaHome = Intent(this, Home::class.java)
            startActivity(telaHome)
            finish()
        }
    }

    private fun setupListener() {
        binding.btnLogin.setOnClickListener{
            autenticarUsuario()
        }
        binding.txtCriarConta.setOnClickListener {
            val cadastro = Intent(this@MainActivity, Cadastro::class.java)
            startActivity(cadastro)
        }
    }

    fun autenticarUsuario(){
        val email = binding.editEmail.text.toString()
        val password = binding.editSenha.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> 
                if (task.isSuccessful) {
                    val telaHome = Intent(this@MainActivity, Home::class.java)
                    startActivity(telaHome)
                    finish()
                } else {
                    val erro = try {
                        throw task.exception ?: Exception("Erro desconhecido")
                    } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        "Usuário não cadastrado."
                    } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                        "E-mail ou senha inválidos."
                    } catch (e: Exception) {
                        "Erro ao fazer login: ${e.message}"
                    }
                    Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
                }
            }
    }
}