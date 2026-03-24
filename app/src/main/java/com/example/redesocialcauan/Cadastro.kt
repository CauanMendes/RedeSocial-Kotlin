package com.example.redesocialcauan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redesocialcauan.databinding.ActivityCadastroBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class Cadastro : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCriarConta.setOnClickListener {
            cadastrarUsuario()
        }

        binding.txtVoltarLogin.setOnClickListener {
            finish()
        }
    }

    private fun cadastrarUsuario(){

        val nomeCompleto = binding.editNomeCompleto.text.toString()
        val email = binding.editEmailCadastro.text.toString()
        val senha = binding.editSenhaCadastro.text.toString()
        val confirmarSenha = binding.editConfirmarSenha.text.toString()

        // verificar campos vazios
        if(nomeCompleto.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()){
            Toast.makeText(this,"Preencha todos os campos",Toast.LENGTH_LONG).show()
            return
        }

        // verificar se as senhas são iguais
        if(senha != confirmarSenha){
            Toast.makeText(this,"As senhas não coincidem",Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth
            .createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->

                if(task.isSuccessful){
                    val db = Firebase.firestore
                    val userData = hashMapOf(
                        "nomeCompleto" to nomeCompleto,
                        "username" to "",
                        "fotoPerfil" to ""
                    )
                    db.collection("usuarios").document(email).set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Cadastro realizado com sucesso!",Toast.LENGTH_LONG).show()
                            val telaHome = Intent(this, Home::class.java)
                            startActivity(telaHome)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"Erro ao salvar dados do perfil",Toast.LENGTH_LONG).show()
                        }
                }else{
                    val erro = try {
                        throw task.exception ?: Exception("Erro desconhecido")
                    } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        "Este e-mail já está em uso."
                    } catch (e: com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                        "A senha precisa ter pelo menos 6 caracteres."
                    } catch (e: Exception) {
                        "Erro ao cadastrar: ${e.message}"
                    }
                    Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
                }

            }
    }
}