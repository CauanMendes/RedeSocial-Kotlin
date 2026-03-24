package com.example.redesocialcauan

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.redesocialcauan.databinding.ActivityNovoPostBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.util.Locale

class NovoPost : AppCompatActivity() {

    private lateinit var binding: ActivityNovoPostBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                obterLocalizacao()
            }
            else -> {
                binding.txtLocation.text = "Localização: Negada"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.imgPost.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }

        // Solicitar permissão de localização
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        cidadeAtual = address.locality ?: address.subAdminArea ?: "Desconhecida"
                        binding.txtLocation.text = "Localização: $cidadeAtual"
                    }
                } catch (e: Exception) {
                    binding.txtLocation.text = "Localização: Erro ao obter cidade"
                }
            } else {
                binding.txtLocation.text = "Localização: Não encontrada"
            }
        }
    }

    private fun publicarPost() {
        val texto = binding.editPostText.text.toString()
        if (binding.imgPost.tag != "has_image") {
            Toast.makeText(this, "Selecione uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val email = auth.currentUser!!.email.toString()
        val base64Image = Base64Converter.drawableToString(binding.imgPost.drawable)

        val db = Firebase.firestore
        val post = hashMapOf(
            "autor" to email,
            "imagemBase64" to base64Image,
            "texto" to texto,
            "cidade" to cidadeAtual,
            "timestamp" to FieldValue.serverTimestamp()
        )

        binding.btnPublicar.isEnabled = false // Evitar múltiplos cliques

        db.collection("posts").add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post publicado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao publicar", Toast.LENGTH_SHORT).show()
                binding.btnPublicar.isEnabled = true
            }
    }
}
