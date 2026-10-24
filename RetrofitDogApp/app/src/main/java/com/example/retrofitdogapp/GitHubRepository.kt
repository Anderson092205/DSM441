package com.example.retrofitdogapp

import com.google.gson.annotations.SerializedName

data class GitHubRepository(
    val id: Long,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    val language: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("html_url") val htmlUrl: String
)
