package com.android.composepokemon.pokemondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.android.composepokemon.data.remote.responses.Pokemon
import com.android.composepokemon.util.Resource





@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()

) {

    // to handle the success, loading and the error state
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading() ){

        value = viewModel.getPokemonInfo(pokemonName)
    }.value
    
    Box(modifier = Modifier
        .fillMaxSize()
        .background(dominantColor)
        .padding(bottom = 16.dp)) {

        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f) // fill top 20% of the screen
                .align(Alignment.TopCenter)
        )
        
        PokemonDetailStateWrapper(pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize/2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                    )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize/2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            )


        Box(contentAlignment =  Alignment.TopCenter,
            modifier = Modifier
            .fillMaxSize()
            ){

            if (pokemonInfo is Resource.Success){

                pokemonInfo.data?.sprites?.let {


                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.front_default)
                            .crossfade(true)
                            .build(),
                        contentDescription = pokemonInfo.data.name,
                        loading = {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary, modifier = Modifier.scale(0.5F)
                            )
                        },
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                        )

                        }
                
                }



            }


        }
        
    }



@Composable
fun PokemonDetailTopSection(

    navController: NavController,
    modifier: Modifier = Modifier
) {

    Box(contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(

                Brush.verticalGradient(
                    listOf(

                        Color.Black,
                        Color.Transparent

                    )
                )

            )){


        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {

                    navController.popBackStack()
                }
        )


    }

}



@Composable
fun PokemonDetailStateWrapper(

    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier

) {

    when(pokemonInfo){

        is Resource.Success -> {

        }

        is Resource.Loading ->{

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )

        }

        is Resource.Error -> {
            
            Text(text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
                )

        }




    }
    
}



    
