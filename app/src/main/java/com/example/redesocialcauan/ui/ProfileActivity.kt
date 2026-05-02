package com.example.redesocialcauan.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocialcauan.auth.UserAuth
import com.example.redesocialcauan.dao.UserDAO
import com.example.redesocialcauan.databinding.ActivityPerfilBinding
import com.example.redesocialcauan.util.Base64Converter

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private val userAuth = UserAuth()
    private val userDAO = UserDAO()

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.profileImage.setImageURI(uri)
            binding.profileImage.tag = "changed"
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarPerfil()

        binding.btnChangePhoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        binding.btnSave.setOnClickListener { salvarPerfil() }
    }

    private fun carregarPerfil() {
        val email = userAuth.emailUsuarioAtual() ?: return
        userDAO.carregar(email) { user ->
            if (user != null) {
                binding.editUsername.setText(user.username)
                binding.editFullName.setText(user.nomeCompleto)
                if (user.fotoPerfil.isNotEmpty()) {
                    try {
                        binding.profileImage.setImageBitmap(Base64Converter.stringToBitmap(user.fotoPerfil))
                    } catch (_: Exception) {}
                }
            }
        }
    }

    private fun salvarPerfil() {
        val email = userAuth.emailUsuarioAtual() ?: return

        val dados = mutableMapOf<String, Any>(
            "username" to binding.editUsername.text.toString(),
            "nomeCompleto" to binding.editFullName.text.toString()
        )

        if (binding.profileImage.tag == "changed") {
            try {
                dados["fotoPerfil"] = Base64Converter.drawableToString(binding.profileImage.drawable)
            } catch (_: Exception) {}
        }

        userDAO.atualizar(
            email = email,
            dados = dados,
            onSuccess = {
                val novaSenha = binding.editNewPassword.text.toString()
                if (novaSenha.isNotEmpty() && novaSenha.length >= 6) {
                    userAuth.alterarSenha(novaSenha) { ok ->
                        if (ok) {
                            Toast.makeText(this, "Perfil e senha salvos!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Perfil salvo, mas ocorreu erro ao alterar senha.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Perfil salvo!", Toast.LENGTH_LONG).show()
                    finish()
                }
            },
            onError = {
                Toast.makeText(this, "Erro ao salvar perfil", Toast.LENGTH_LONG).show()
            }
        )
    }
}
