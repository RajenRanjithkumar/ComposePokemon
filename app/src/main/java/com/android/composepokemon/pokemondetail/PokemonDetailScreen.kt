package com.android.composepokemon.pokemondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.android.composepokemon.R
import com.android.composepokemon.data.remote.responses.Pokemon
import com.android.composepokemon.data.remote.responses.Type
import com.android.composepokemon.util.Resource
import com.android.composepokemon.util.parseStatToColor
import com.android.composepokemon.util.parseTypeToColor
import java.util.Locale
import kotlin.math.round


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
                    top = topPadding + pokemonImageSize / 2f,
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
                    top = topPadding + pokemonImageSize / 2f,
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

            PokemonDetailSection(
                pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp))

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
                    .padding(10.dp)
                )

        }




    }
    
}
@Composable
fun PokemonDetailSection(

    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier

) {

    val scrollState = rememberScrollState()

    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        
        
        Text(text = "#${pokemonInfo.id} ${pokemonInfo.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 30.sp

        )
        
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(pokemonWeight = pokemonInfo.weight, pokemonHeight = pokemonInfo.height)


        
    }

}


@Composable
fun PokemonTypeSection(
    types: List<Type>

) {


    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
        ) {

                for( type in types){

                    Box(modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                        .clip(CircleShape)
                        .background(parseTypeToColor(type))
                        .height(35.dp)
                        ){
                        
                        Text(text = type.type.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                            )

                    }

                }


    }
    
}


@Composable
fun PokemonDetailDataSection(

    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp

) {
    val pokemonWeightInKg = remember {

        round(pokemonWeight * 100f ) / 1000f
    }

    val pokemonHeightInMeters = remember {

        round(pokemonHeight * 100f ) / 1000f
    }
    
    Row(modifier = Modifier
        .fillMaxWidth()) {

        PokemonDetailDataSectionItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "Kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier
            .size(1.dp, sectionHeight)
            .background(Color.LightGray))

        PokemonDetailDataSectionItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
            modifier = Modifier.weight(1f))
        
    }
    
    
    
}

@Composable
fun PokemonDetailDataSectionItem(

    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier

) {
    
    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        
        Icon(painter = dataIcon, contentDescription =  null)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$dataValue$dataUnit" , color = MaterialTheme.colorScheme.onSurface)


    }

}
    
