package com.example.barcodescanner

import com.google.gson.annotations.SerializedName

data class Product(
    @field:SerializedName("code") val code: String?,
    @field:SerializedName("name") val name: String?,
    @field:SerializedName("calorie") val calorie: Float?,
    @field:SerializedName("fat") val fat: Float?,
    @field:SerializedName("saturated") val saturated: Float?,
    @field:SerializedName("carb") val carb: Float?,
    @field:SerializedName("sugar") val sugar: Float?,
    @field:SerializedName("protein") val protein: Float?,
    @field:SerializedName("sodium") val sodium: Float?
)