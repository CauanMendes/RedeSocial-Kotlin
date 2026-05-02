package com.example.redesocialcauan.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialcauan.R
import com.example.redesocialcauan.model.Post

class PostAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAuthor: TextView = itemView.findViewById(R.id.txtAuthor)
        val txtCity: TextView = itemView.findViewById(R.id.txtCity)
        val txtPostText: TextView = itemView.findViewById(R.id.txtPostText)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.txtAuthor.text = post.autor
        holder.txtCity.text = post.cidade
        holder.txtPostText.text = post.texto

        try {
            val decodedBytes = Base64.decode(post.imagemBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imgPost.setImageBitmap(bitmap)
        } catch (e: Exception) {
            holder.imgPost.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount() = posts.size

    fun addPosts(newPosts: List<Post>) {
        val startPosition = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(startPosition, newPosts.size)
    }

    fun clear() {
        val size = posts.size
        posts.clear()
        notifyItemRangeRemoved(0, size)
    }
}
