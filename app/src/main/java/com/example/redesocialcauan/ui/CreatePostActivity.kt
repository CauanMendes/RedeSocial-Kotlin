package com.example.redesocialcauan.ui

import android.Manifest
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.redesocialcauan.auth.UserAuth
import com.example.redesocialcauan.dao.PostDAO
import com.example.redesocialcauan.databinding.ActivityNovoPostBinding
import com.example.redesocialcauan.util.Base64Converter
import com.example.redesocialcauan.util.LocalizacaoHelper

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovoPostBinding
    private lateinit var localizacaoHelper: LocalizacaoHelper
    private val userAuth = UserAuth()
    private val postDAO = PostDAO()
    private var cidadeAtual: String = "Desconhecida"

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.imgPost.setImageURI(uri)
            binding.imgPost.tag = "has_image"
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val concedida = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (concedida) {
            obterLocalizacao()
        } else {
            binding.txtLocation.text = "Localização: Negada"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localizacaoHelper = LocalizacaoHelper(this)

        binding.imgPost.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnPublicar.setOnClickListener { publicarPost() }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun obterLocalizacao() {
        localizacaoHelper.obterLocalizacaoAtual(object : LocalizacaoHelper.Callback {
            override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
                cidadeAtual = endereco.locality
                    ?: endereco.subAdminArea
                    ?: endereco.adminArea
                    ?: "Desconhecida"
                binding.txtLocation.text = "Localização: $cidadeAtual"
            }

            override fun onErro(mensagem: String) {
                binding.txtLocation.text = "Localização: $mensagem"
            }
        })
    }

    private fun publicarPost() {
        val texto = binding.editPostText.text.toString()
        if (binding.imgPost.tag != "has_image") {
            Toast.makeText(this, "Selecione uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val email = userAuth.emailUsuarioAtual()
        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Image = Base64Converter.drawableToString(binding.imgPost.drawable)
        binding.btnPublicar.isEnabled = false

        postDAO.publicar(
            autor = email,
            texto = texto,
            cidade = cidadeAtual,
            imagemBase64 = base64Image,
            onSuccess = {
                Toast.makeText(this, "Post publicado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = {
                Toast.makeText(this, "Erro ao publicar", Toast.LENGTH_SHORT).show()
                binding.btnPublicar.isEnabled = true
            }
        )
    }
}
