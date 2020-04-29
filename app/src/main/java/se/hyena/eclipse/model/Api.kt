package se.hyena.eclipse.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "7e46ce49f4e6d46ff51eed9969c7d842",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = "7e46ce49f4e6d46ff51eed9969c7d842",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = "7e46ce49f4e6d46ff51eed9969c7d842",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("search/movie")
    fun getSearchResults(
        @Query("api_key") apiKey: String = "7e46ce49f4e6d46ff51eed9969c7d842",
        @Query("page") page: Int
    ): Call<GetMoviesResponse>
}