package com.example.retrofitdogapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RepositoryAdapter(private val onClick: (GitHubRepository) -> Unit) :
    ListAdapter<GitHubRepository, RepositoryAdapter.RepositoryViewHolder>(Diff) {
    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.repositoryName)
        val descripcion: TextView = view.findViewById(R.id.repositoryDescription)
        val detalles: TextView = view.findViewById(R.id.repositoryDetails)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RepositoryViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false))

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repo = getItem(position)
        val context = holder.itemView.context
        holder.nombre.text = repo.fullName
        holder.descripcion.text = repo.description?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.github_no_description)
        holder.detalles.text = context.getString(R.string.github_repository_details,
            repo.language ?: context.getString(R.string.github_no_language), repo.stars)
        holder.itemView.contentDescription = context.getString(R.string.github_open_repository, repo.fullName)
        holder.itemView.setOnClickListener { onClick(repo) }
    }
    private object Diff : DiffUtil.ItemCallback<GitHubRepository>() {
        override fun areItemsTheSame(old: GitHubRepository, new: GitHubRepository) = old.id == new.id
        override fun areContentsTheSame(old: GitHubRepository, new: GitHubRepository) = old == new
    }
}
