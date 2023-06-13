package com.android.composepokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.composepokemon.pokemondetail.PokemonDetailScreen
import com.android.composepokemon.pokemonlist.PokemonListScreen
import com.android.composepokemon.ui.theme.ComposePokemonTheme
import dagger.hilt.android.AndroidEntryPoint

//https://www.youtube.com/watch?v=UR-lrDimmPI&list=PLQkwcJG4YTCTimTCpEL5FZgaWdIZQuB7m&index=9



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePokemonTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "pokemon_list_screen"
                ){

                    composable("pokemon_list_screen"){
                        
                        PokemonListScreen(navController = navController)


                    }

                    composable("pokemon_details_screen/{dominantColor}/{pokemonName}",
                    arguments = listOf(
                        navArgument("dominantColor"){
                            type = NavType.IntType
                        },
                        navArgument("pokemonName"){
                            type = NavType.StringType
                        }
                        )
                    ){

                        val dominantColor = remember {
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) } ?: Color.White // if the color parameter is empty

                        }

                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }

                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase()?:"",
                            navController = navController)



                    }

                }

            }
        }
    }
}



