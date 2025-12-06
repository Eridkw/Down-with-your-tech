package com.example.downwithyourtech

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun refreshDataFromApi() {
        // Simulamos carga
        delay(1500)
        val serverData = listOf(
            ProductEntity("1", "Galaxy Buds 3 Pro", 3028.00, "", "Samsung Store", "Audio Hi-Fi"),
            ProductEntity("2", "RTX 4060", 6500.00, "", "Amazon", "Gráfica potente"),
            ProductEntity("3", "iPhone 15", 18000.00, "", "Liverpool", "El último iPhone")
        )
        productDao.insertAll(serverData)
    }
}