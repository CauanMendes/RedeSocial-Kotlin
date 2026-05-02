package com.example.redesocialcauan.ui

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialcauan.adapter.PostAdapter
import com.example.redesocialcauan.auth.UserAuth
import com.example.redesocialcauan.dao.PostDAO
import com.example.redesocialcauan.dao.UserDAO
import com.example.redesocialcauan.databinding.ActivityHomeBinding
import com.example.redesocialcauan.util.Base64Converter
import com.google.firebase.firestore.DocumentSnapshot

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var postAdapter: PostAdapter
    private val postDAO = PostDAO()
    private val userDAO = UserDAO()
    private val userAuth = UserAuth()
    private var lastVisible: DocumentSnapshot? = null
    private var isSearching = false
    private var isLoading = false
    private val postLimit: Long = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postAdapter = PostAdapter(
            posts = mutableListOf(),
            currentUserEmail = userAuth.emailUsuarioAtual(),
            onDelete = { post, position ->
                postDAO.excluir(
                    id = post.id,
                    onSuccess = {
                        postAdapter.removeAt(position)
                        Toast.makeText(this, "Post excluído", Toast.LENGTH_SHORT).show()
                    },
                    onError = {
                        Toast.makeText(this, "Erro ao excluir post", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = postAdapter

        loadPosts()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !isSearching && !isLoading) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == postAdapter.itemCount - 1) {
                        loadMorePosts()
                    }
                }
            }
        })

        binding.btnPerfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.btnNovoPost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) searchPostsByCity(query) else resetSearch()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) resetSearch()
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isSearching) loadPosts()
        carregarFotoPerfil()
    }

    private fun carregarFotoPerfil() {
        val email = userAuth.emailUsuarioAtual() ?: return
        userDAO.carregar(email) { user ->
            val foto = user?.fotoPerfil
            if (!foto.isNullOrEmpty()) {
                try {
                    binding.btnPerfil.setImageBitmap(Base64Converter.stringToBitmap(foto))
                } catch (_: Exception) {}
            }
        }
    }

    private fun loadPosts() {
        isLoading = true
        postDAO.carregarFeed(
            limit = postLimit,
            onSuccess = { posts, last ->
                postAdapter.clear()
                postAdapter.addPosts(posts)
                lastVisible = last
                isLoading = false
            },
            onError = {
                Toast.makeText(this, "Erro ao carregar feed", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    private fun loadMorePosts() {
        val last = lastVisible ?: return
        isLoading = true
        postDAO.carregarMais(
            lastVisible = last,
            limit = postLimit,
            onSuccess = { posts, newLast ->
                postAdapter.addPosts(posts)
                lastVisible = newLast
                isLoading = false
            },
            onError = { isLoading = false }
        )
    }

    private fun searchPostsByCity(city: String) {
        isSearching = true
        isLoading = true
        postDAO.buscarPorCidade(
            cidade = city,
            onSuccess = { posts ->
                postAdapter.clear()
                postAdapter.addPosts(posts)
                isLoading = false
            },
            onError = {
                Toast.makeText(this, "Erro na busca", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    private fun resetSearch() {
        isSearching = false
        loadPosts()
    }
}
