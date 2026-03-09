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
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> if (task.isSuccessful) {
                val telaHome = Intent(
                    this@MainActivity,
                    Home::class.java
                )
                startActivity(telaHome)
            } else {
                Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
            }
            }
    }
}