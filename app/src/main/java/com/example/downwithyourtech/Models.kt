package com.example.downwithyourtech

import java.io.Serializable

class Models {
    data class Product(
        val id: String,
        val name: String,
        val imageUrl: String,
        val price: Double,
        val storeName: String,
        val rating: Float,
        val specs: String
    ) : Serializable

    data class StoreOption(
        val storeName: String,
        val price: Double,
        val url: String
    )

}