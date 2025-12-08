package com.example.downwithyourtech.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

data class ResultadoScraping(
    val nombre: String,
    val precio: Double,
    val link: String,
    val tienda: String
)

class WebScraper {

    // Ejecuta en segundo plano (IO)
    suspend fun buscarEnMercadoLibre(producto: String): ResultadoScraping? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. URL de búsqueda
                val busquedaFormato = producto.replace(" ", "-")
                val url = "https://listado.mercadolibre.com.mx/$busquedaFormato"

                // 2. Conexión (Timeout alto por si el internet es lento)
                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Android 13; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0")
                    .timeout(10000)
                    .get()

                // 3. Selectores (Actualizados y Genéricos)
                // Buscamos el primer item de la lista de resultados
                val primerProducto = doc.select("li.ui-search-layout__item").first()
                    ?: doc.select("div.ui-search-result__wrapper").first() // Intento alternativo

                if (primerProducto != null) {
                    // LINK
                    val linkTag = primerProducto.select("a").first()
                    val linkReal = linkTag?.attr("href") ?: ""

                    // NOMBRE
                    val tituloTag = primerProducto.select("h2").first() ?: primerProducto.select("span.ui-search-item__title").first()
                    val nombreReal = tituloTag?.text() ?: "Producto Encontrado"

                    // PRECIO (La parte difícil: Limpiar "$ 1,200.00")
                    val precioContainer = primerProducto.select("div.ui-search-price__second-line").first()
                        ?: primerProducto.select("span.andes-money-amount__fraction").first()

                    var precioTexto = precioContainer?.text() ?: "0"

                    // Limpieza agresiva: Quitar todo lo que no sea número o punto
                    precioTexto = precioTexto.replace(Regex("[^0-9.]"), "")

                    val precioReal = precioTexto.toDoubleOrNull() ?: 0.0

                    // Solo devolvemos si encontramos un precio válido
                    if (precioReal > 0) {
                        return@withContext ResultadoScraping(
                            nombre = nombreReal,
                            precio = precioReal,
                            link = linkReal,
                            tienda = "Mercado Libre (En Vivo)"
                        )
                    }
                }
                return@withContext null

            } catch (e: Exception) {
                // Si falla, solo imprimimos el error en consola pero NO crasheamos la app
                Log.e("WebScraper", "Error al buscar: ${e.message}")
                return@withContext null
            }
        }
    }
}