package com.android.composepokemon.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusEventModifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.android.composepokemon.R
import com.android.composepokemon.data.models.PokedexListEntry
import com.android.composepokemon.data.remote.responses.PokemonList
import com.android.composepokemon.ui.theme.Purple80
import com.android.composepokemon.ui.theme.RobotoCondensed
import com.google.accompanist.coil.CoilImage

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
){


    Surface(

        //color = MaterialTheme.colorScheme.primary,
        color = Purple80,
        modifier = Modifier.fillMaxSize()
    ) {
        
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            
            Image(painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally))

            SearchBar(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                hint = "Search"){

                viewModel.searchPokemonList(it)

            }
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController)


        }

    }

}

@Composable
fun SearchBar(modifier: Modifier, hint: String, onSearch: (String) -> Unit ={}){

    var text by remember {

        mutableStateOf("")

    }

    var isHintDisplayed by remember {

        mutableStateOf(hint != "")

    }


    Box(modifier = modifier){
        
        BasicTextField(value = text, onValueChange = {
            text = it
            onSearch(it)
        },
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(25.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    //isHintDisplayed = it != FocusState.Active
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if(isHintDisplayed){
            
            Text(text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp))
            
        }
    }

}


@Composable
fun PokemonList(navController: NavController,
                viewModel: PokemonListViewModel = hiltViewModel(),
){

    val pokemonList by remember {
        viewModel.pokemonList
    }
    val endReached by remember {
        viewModel.endReached
    }
    val loadError by remember {
        viewModel.loadError
    }
    val isLoading by remember {
        viewModel.isLoading
    }

    val isSearching by remember {
        viewModel.isSearching
    }


    LazyColumn(contentPadding = PaddingValues(16.dp)){

        val itemCount = if(pokemonList.size%2 == 0){
            pokemonList.size/2
        }else{
            pokemonList.size/2+1
        }

        items(itemCount){

            // to detect if we have scrolled to the bottom
            if(it >= itemCount-1 && !endReached && !isLoading && !isSearching){

                viewModel.loadPokemonPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)

        }

    }
    
    
    Box(

        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()

    ) {

        if(isLoading){

            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        
        if (loadError.isNotEmpty()){
            
            RetrySection(error = loadError) {

                // call the view model when we click the button
                viewModel.loadPokemonPaginated()
                
            }
            
        }


    }



}



@Composable
fun PokedexEntry(entry: PokedexListEntry,
                 navController: NavController,
                 modifier: Modifier,
                 viewModel: PokemonListViewModel = hiltViewModel()
){

    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip((RoundedCornerShape(10.dp)))
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {

                navController.navigate(
                    "pokemon_details_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )

            }
    ){


        Column {


            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = entry.pokemonName,
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary, modifier = Modifier.scale(0.5F)
                    )
                },
                success = { success ->
                    viewModel.calcDominantColor(success.result.drawable){
                        dominantColor = it
                    }
                    SubcomposeAsyncImageContent()
                },
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )
            
//            CoilImage( request = ImageRequest.Builder(LocalContext.current)
//                .data(entry.imageUrl)
//                .target{
//
//                    viewModel.calcDominantColor(it){ color ->
//
//                        dominantColor = color
//
//
//                    }
//
//                }
//                .build(),
//                contentDescription = entry.pokemonName,
//                fadeIn = true,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally)
//            )
//            {
//                //display progress bar while loading the Image
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.scale(0.5f)
//                )
//
//            }

            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                )
            

        }

    }


}

@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){

    Column {

        Row {

            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController
                , modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            if(entries.size >= rowIndex * 2 +1){

                PokedexEntry(
                    //entry = entries[rowIndex * 2+1]
                    entry = entries[rowIndex * 2 +1],
                    navController = navController
                    , modifier = Modifier.weight(1f)
                )

            } else{
                Spacer(modifier = Modifier.weight(1f))

            }


        }
        Spacer(modifier = Modifier.height(16.dp))


    }


}


@Composable
fun RetrySection(error:String, onRetry: () -> Unit){

    Column{
        
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRetry()},
            modifier = Modifier.align(CenterHorizontally)
            ) {
            
            Text(text = "Retry")
            
        }

        

    }

}















@Composable
fun PokedexLazyRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){

    LazyVerticalGrid(columns = GridCells.Fixed(2),
        modifier = Modifier.scale(1.01f)){

        items(entries.size){

            PokedexEntry(
                entry = entries[it],
                navController = navController,
                modifier = Modifier
            )

        }
    }


}

