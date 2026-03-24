package com.example.redesocialcauan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redesocialcauan.databinding.ActivityPerfilBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class Perfil : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    // ActivityResult para abrir a galeria
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

    private fun salvarPerfil(){

        val firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null){

            val email = firebaseAuth.currentUser!!.email.toString()
            val username = binding.editUsername.text.toString()
            val nomeCompleto = binding.editFullName.text.toString()

            val db = Firebase.firestore

            val dados = mutableMapOf<String, Any>(
                "username" to username,
                "nomeCompleto" to nomeCompleto
            )
            
            if (binding.profileImage.tag == "changed") {
                try {
                    dados["fotoPerfil"] = Base64Converter.drawableToString(binding.profileImage.drawable)
                } catch (e: Exception) {}
            }

            // Salva dados no firestore com merge para não apagar a foto antiga se não foi alterada
            db.collection("usuarios")
                .document(email)
                .set(dados, SetOptions.merge())
                .addOnSuccessListener {
                    val novaSenha = binding.editNewPassword.text.toString()
                    if (novaSenha.isNotEmpty() && novaSenha.length >= 6) {
                        firebaseAuth.currentUser!!.updatePassword(novaSenha)
                            .addOnCompleteListener { passTask ->
                                if (passTask.isSuccessful) {
                                    Toast.makeText(this,"Perfil e senha salvos!",Toast.LENGTH_LONG).show()
                                    finish() // Volta pra home
                                } else {
                                    Toast.makeText(this,"Perfil salvo, mas ocorreu erro ao alterar senha.",Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this,"Perfil salvo!",Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Erro ao salvar perfil",Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        carregarPerfil()

        // botão alterar foto
        binding.btnChangePhoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        
        binding.btnSave.setOnClickListener {
            salvarPerfil()
        }
    }
    
    private fun carregarPerfil() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return
        val db = Firebase.firestore

        db.collection("usuarios").document(user.email.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.editUsername.setText(document.getString("username") ?: "")
                    binding.editFullName.setText(document.getString("nomeCompleto") ?: "")
                    
                    val fotoString = document.getString("fotoPerfil")
                    if (!fotoString.isNullOrEmpty()) {
                        try {
                            val bitmap = Base64Converter.stringToBitmap(fotoString)
                            binding.profileImage.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            // Erro ao converter a imagem, mantém a foto padrão
                        }
                    }
                }
            }
    }
}