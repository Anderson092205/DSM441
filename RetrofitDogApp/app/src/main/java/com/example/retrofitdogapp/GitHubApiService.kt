package com.example.retrofitdogapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    @Headers("Accept: application/vnd.github+json", "X-GitHub-Api-Version: 2026-03-10", "User-Agent: RetrofitDogApp")
    @GET("users/{username}/repos")
    fun getRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "full_name"
    ): Call<List<GitHubRepository>>
}
