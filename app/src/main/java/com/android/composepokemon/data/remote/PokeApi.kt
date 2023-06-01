package com.android.composepokemon.data.remote

import com.android.composepokemon.data.remote.responses.Pokemon
import com.android.composepokemon.data.remote.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemonList(

        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(

        @Path("name") name: String
    ): Pokemon
}