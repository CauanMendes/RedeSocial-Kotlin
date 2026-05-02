package com.example.redesocialcauan.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.redesocialcauan.R
import com.example.redesocialcauan.model.Post

class PostAdapter(
    private val posts: MutableList<Post>,
    private val currentUserEmail: String?,
    private val onDelete: (Post, Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAuthor: TextView = itemView.findViewById(R.id.txtAuthor)
        val txtCity: TextView = itemView.findViewById(R.id.txtCity)
        val txtPostText: TextView = itemView.findViewById(R.id.txtPostText)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val btnDeletePost: ImageButton = itemView.findViewById(R.id.btnDeletePost)
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

        if (currentUserEmail != null && post.autor == currentUserEmail) {
            holder.btnDeletePost.visibility = View.VISIBLE
            holder.btnDeletePost.setOnClickListener {
                onDelete(post, holder.adapterPosition)
            }
        } else {
            holder.btnDeletePost.visibility = View.GONE
            holder.btnDeletePost.setOnClickListener(null)
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

    fun removeAt(position: Int) {
        if (position < 0 || position >= posts.size) return
        posts.removeAt(position)
        notifyItemRemoved(position)
    }
}
