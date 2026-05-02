package com.example.redesocialcauan.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocialcauan.auth.UserAuth
import com.example.redesocialcauan.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userAuth = UserAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        if (userAuth.estaLogado()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun setupListener() {
        binding.btnLogin.setOnClickListener { autenticarUsuario() }
        binding.txtCriarConta.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun autenticarUsuario() {
        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        userAuth.login(
            email = email,
            senha = senha,
            onSuccess = {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onError = { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        )
    }
}
