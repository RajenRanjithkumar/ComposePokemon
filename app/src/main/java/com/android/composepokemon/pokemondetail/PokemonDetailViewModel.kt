package com.android.composepokemon.pokemondetail

import androidx.lifecycle.ViewModel
import com.android.composepokemon.data.remote.responses.Pokemon
import com.android.composepokemon.repository.PokemonRepository
import com.android.composepokemon.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PokemonDetailViewModel @Inject constructor(

    private val repository: PokemonRepository

) : ViewModel(){

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {

        return repository.getPokemonInfo(pokemonName)
    }

}