package com.example.redesocialcauan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.redesocialcauan.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth

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
    }

    private fun cadastrarUsuario(){

        val email = binding.editEmailCadastro.text.toString()
        val senha = binding.editSenhaCadastro.text.toString()
        val confirmarSenha = binding.editConfirmarSenha.text.toString()

        // verificar campos vazios
        if(email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()){
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

                    Toast.makeText(this,"Cadastro realizado!",Toast.LENGTH_LONG).show()

                    val telaHome = Intent(this, Home::class.java)
                    startActivity(telaHome)
                    finish()

                }else{
                    Toast.makeText(this,"Erro ao cadastrar usuário",Toast.LENGTH_LONG).show()
                }

            }
    }
}