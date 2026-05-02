package com.example.redesocialcauan.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocialcauan.auth.UserAuth
import com.example.redesocialcauan.dao.UserDAO
import com.example.redesocialcauan.databinding.ActivityCadastroBinding
import com.example.redesocialcauan.model.User

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private val userAuth = UserAuth()
    private val userDAO = UserDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCriarConta.setOnClickListener { cadastrarUsuario() }
        binding.txtVoltarLogin.setOnClickListener { finish() }
    }

    private fun cadastrarUsuario() {
        val nomeCompleto = binding.editNomeCompleto.text.toString()
        val email = binding.editEmailCadastro.text.toString()
        val senha = binding.editSenhaCadastro.text.toString()
        val confirmarSenha = binding.editConfirmarSenha.text.toString()

        if (nomeCompleto.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            return
        }
        if (senha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
            return
        }

        userAuth.cadastrar(
            email = email,
            senha = senha,
            onSuccess = {
                userDAO.salvar(
                    user = User(email = email, nomeCompleto = nomeCompleto),
                    onSuccess = {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onError = {
                        Toast.makeText(this, "Erro ao salvar dados do perfil", Toast.LENGTH_LONG).show()
                    }
                )
            },
            onError = { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        )
    }
}
