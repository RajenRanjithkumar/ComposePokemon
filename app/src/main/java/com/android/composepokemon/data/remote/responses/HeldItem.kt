package com.android.composepokemon.data.remote.responses

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)