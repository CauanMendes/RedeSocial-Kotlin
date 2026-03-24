package com.example.redesocialcauan

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialcauan.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var postAdapter: PostAdapter
    private val db = Firebase.firestore
    private var lastVisible: DocumentSnapshot? = null
    private var isSearching = false
    private var isLoading = false
    private val postLimit: Long = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        postAdapter = PostAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = postAdapter

        // Load Initial Posts
        loadPosts()

        // Paginacao
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

        // Navegacao
        binding.btnPerfil.setOnClickListener {
            val telaPerfil = Intent(this, Perfil::class.java)
            startActivity(telaPerfil)
        }

        binding.btnNovoPost.setOnClickListener {
            val telaNovoPost = Intent(this, NovoPost::class.java)
            startActivity(telaNovoPost)
        }

        // Pesquisa
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchPostsByCity(query)
                } else {
                    resetSearch()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    resetSearch()
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Quando volta da tela de NovoPost, recarregar o feed para mostrar o novo
        if (!isSearching) {
            loadPosts()
        }
    }

    private fun loadPosts() {
        isLoading = true
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(postLimit)
            .get()
            .addOnSuccessListener { result ->
                postAdapter.clear()
                val newPosts = mutableListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    newPosts.add(post.copy(id = document.id))
                }
                postAdapter.addPosts(newPosts)

                if (result.size() > 0) {
                    lastVisible = result.documents[result.size() - 1]
                }
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar feed", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    private fun loadMorePosts() {
        if (lastVisible == null) return
        isLoading = true
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(lastVisible!!)
            .limit(postLimit)
            .get()
            .addOnSuccessListener { result ->
                val newPosts = mutableListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    newPosts.add(post.copy(id = document.id))
                }
                postAdapter.addPosts(newPosts)

                if (result.size() > 0) {
                    lastVisible = result.documents[result.size() - 1]
                } else {
                    lastVisible = null // Não tem mais
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    private fun searchPostsByCity(city: String) {
        isSearching = true
        isLoading = true
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50) // Busca os ultimos 50 para filtrar localmente (ideal para busca sem distinção de maiúsculas)
            .get()
            .addOnSuccessListener { result ->
                postAdapter.clear()
                val newPosts = mutableListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    if (post.cidade.contains(city, ignoreCase = true)) {
                        newPosts.add(post.copy(id = document.id))
                    }
                }
                postAdapter.addPosts(newPosts)
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro na busca", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    private fun resetSearch() {
        isSearching = false
        loadPosts()
    }
}