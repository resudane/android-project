package com.pnc.androidproject.features.products.data

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("productId", alternate = ["ProductId", "id", "Id"])
    val productId: Int,
    @SerializedName("name", alternate = ["Name", "productName", "ProductName"])
    val name: String?,
    @SerializedName("productNumber", alternate = ["ProductNumber"])
    val productNumber: String?,
    @SerializedName("summary", alternate = ["Summary", "description", "Description"])
    val summary: String?,
    @SerializedName("thumbnailPhoto", alternate = ["ThumbnailPhoto", "thumbnail", "Thumbnail"])
    val thumbnailPhoto: String?,
    @SerializedName("thumbnailPhotoFileName", alternate = ["ThumbnailPhotoFileName"])
    val thumbnailPhotoFileName: String?,
    @SerializedName("warranty", alternate = ["Warranty"])
    val warranty: String?,
    @SerializedName("color", alternate = ["Color"])
    val color: String?,
    @SerializedName("listPrice", alternate = ["ListPrice", "price", "Price"])
    val listPrice: Double?,
    @SerializedName("stockLevel", alternate = ["Quantity", "stock", "Stock"])
    val stockLevel: Int? = null
)
