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
import com.google.firebase.firestore.firestore

class Perfil : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    // ActivityResult para abrir a galeria
    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {
            binding.profileImage.setImageURI(uri)
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

            val fotoPerfilString = Base64Converter.drawableToString(
                binding.profileImage.drawable
            )

            val db = Firebase.firestore

            val dados = hashMapOf(
                "username" to username,
                "nomeCompleto" to nomeCompleto,
                "fotoPerfil" to fotoPerfilString
            )

            db.collection("usuarios")
                .document(email)
                .set(dados)
                .addOnSuccessListener {

                    Toast.makeText(this,"Perfil salvo!",Toast.LENGTH_LONG).show()

                    val telaHome = Intent(this, Home::class.java)
                    startActivity(telaHome)
                    finish()
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

        // botão alterar foto
        binding.btnChangePhoto.setOnClickListener {

            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )

        }
    }
}