package com.example.downwithyourtech.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.downwithyourtech.R
import com.example.downwithyourtech.data.DatabaseHelper
import com.example.downwithyourtech.data.WebScraper

// ==========================================
// 1. CONSTANTES Y MODELOS
// ==========================================
val TechBackground = Color(0xFF000040)
val NeonGreen = Color(0xFFCCFF00)
val White = Color.White
val GrayPlaceholder = Color(0xFFF5F5F5)
val GoldStar = Color(0xFFFFD700)

enum class CategoriaType { TODOS, MOVILES, GAMING, AUDIO, TV }

data class OpcionTienda(
    val nombreTienda: String,
    val precio: Double,
    val rating: Double,
    val urlEnlace: String
)

data class ProductoUi(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaType,
    val specs: String,
    val opciones: List<OpcionTienda>,
    val imagenUrl: String? = null,
    val iconoDefault: ImageVector = Icons.Default.Devices,
    val esFavorito: Boolean = false // <--- NUEVO CAMPO PARA CONTROLAR FAVORITOS
) {
    fun obtenerMejorPrecio(): Double = opciones.minOfOrNull { it.precio } ?: 0.0
}

// --- CATÁLOGO EXPANDIDO (15 PRODUCTOS CON FOTOS DE UNSPLASH) ---
val catalogoCompleto = listOf(
    // --- GAMING ---
    ProductoUi(1, "RTX 4060 8GB", CategoriaType.GAMING, "8GB GDDR6, DLSS 3.0", listOf(OpcionTienda("Amazon", 6500.0, 4.8, "https://www.amazon.com.mx/s?k=rtx+4060"), OpcionTienda("MercadoLibre", 6300.0, 4.7, "https://listado.mercadolibre.com.mx/rtx-4060")), imagenUrl = "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Computer),
    ProductoUi(5, "Logitech G502 X", CategoriaType.GAMING, "Hero 25K, Híbrido óptico", listOf(OpcionTienda("Amazon", 1500.0, 4.8, "https://www.amazon.com.mx/s?k=logitech+g502x"), OpcionTienda("Cyberpuerta", 1450.0, 4.5, "https://www.cyberpuerta.mx/")), imagenUrl = "https://images.unsplash.com/photo-1615663245857-acda6b247195?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Mouse),
    ProductoUi(6, "Teclado Mecánico RGB", CategoriaType.GAMING, "Switches Rojos, 60%", listOf(OpcionTienda("Amazon", 800.0, 4.4, "https://www.amazon.com.mx/"), OpcionTienda("MercadoLibre", 750.0, 4.3, "https://listado.mercadolibre.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Keyboard),
    ProductoUi(7, "Monitor Gamer 165Hz", CategoriaType.GAMING, "24 pulgadas, 1ms IPS", listOf(OpcionTienda("Amazon", 3500.0, 4.6, "https://www.amazon.com.mx/"), OpcionTienda("Walmart", 3200.0, 4.5, "https://www.walmart.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.DesktopWindows),
    ProductoUi(15, "Steam Deck OLED", CategoriaType.GAMING, "1TB, Pantalla OLED HDR", listOf(OpcionTienda("Amazon (Importado)", 14000.0, 4.9, "https://www.amazon.com.mx/"), OpcionTienda("MercadoLibre", 13500.0, 4.8, "https://listado.mercadolibre.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1698668687273-61d373594124?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Gamepad),


    // --- AUDIO ---
    ProductoUi(2, "Galaxy Buds 3 Pro", CategoriaType.AUDIO, "ANC Adaptativo, 24bit", listOf(OpcionTienda("Samsung", 3200.0, 4.9, "https://www.samsung.com/mx/"), OpcionTienda("Amazon", 2900.0, 4.7, "https://www.amazon.com.mx/s?k=galaxy+buds+3+pro")), imagenUrl = "https://images.unsplash.com/photo-1610427303867-b703e72dc07e?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Headphones),
    ProductoUi(8, "Sony WH-1000XM5", CategoriaType.AUDIO, "Noise Cancelling Líder", listOf(OpcionTienda("Sony Store", 6500.0, 4.9, "https://store.sony.com.mx/"), OpcionTienda("Amazon", 5800.0, 4.8, "https://www.amazon.com.mx/s?k=sony+wh1000xm5")), imagenUrl = "https://images.unsplash.com/photo-1613040809024-b4ef748fc483?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Headphones),
    ProductoUi(9, "Bocina JBL Flip 6", CategoriaType.AUDIO, "Waterproof IP67, 12h batería", listOf(OpcionTienda("Amazon", 1800.0, 4.7, "https://www.amazon.com.mx/"), OpcionTienda("Liverpool", 2100.0, 4.8, "https://www.liverpool.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Speaker),

    // --- MÓVILES ---
    ProductoUi(3, "iPhone 15 Pro Max", CategoriaType.MOVILES, "A17 Pro, Titanio, Zoom 5x", listOf(OpcionTienda("Apple", 28999.0, 5.0, "https://www.apple.com/mx/shop"), OpcionTienda("Amazon", 27500.0, 4.8, "https://www.amazon.com.mx/s?k=iphone+15+pro+max")), imagenUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Smartphone),
    ProductoUi(10, "Samsung S24 Ultra", CategoriaType.MOVILES, "Galaxy AI, Zoom 100x, S-Pen", listOf(OpcionTienda("Samsung", 26000.0, 4.9, "https://www.samsung.com/mx/"), OpcionTienda("Amazon", 24500.0, 4.7, "https://www.amazon.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Smartphone),
    ProductoUi(11, "Pixel 8 Pro", CategoriaType.MOVILES, "La mejor cámara con IA Google", listOf(OpcionTienda("Amazon (Importado)", 18000.0, 4.6, "https://www.amazon.com.mx/"), OpcionTienda("MercadoLibre", 17500.0, 4.5, "https://listado.mercadolibre.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1696863729307-49840934da21?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Smartphone),

    // --- TV ---
    ProductoUi(4, "Xiaomi TV Q2 55\"", CategoriaType.TV, "QLED 4K, Google TV", listOf(OpcionTienda("Walmart", 8500.0, 4.4, "https://www.walmart.com.mx/buscar?q=xiaomi+tv"), OpcionTienda("Amazon", 8900.0, 4.6, "https://www.amazon.com.mx/s?k=xiaomi+tv+q2")), imagenUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Tv),
    ProductoUi(12, "LG OLED C3 65\"", CategoriaType.TV, "OLED evo, Negros Perfectos, 120Hz", listOf(OpcionTienda("Liverpool", 25000.0, 4.9, "https://www.liverpool.com.mx/"), OpcionTienda("Costco", 24500.0, 4.8, "https://www.costco.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1601944179066-29786cb9d32a?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Tv),
    ProductoUi(13, "Samsung Neo QLED 55\"", CategoriaType.TV, "Mini LED, Brillo Intenso", listOf(OpcionTienda("Samsung", 21000.0, 4.7, "https://www.samsung.com/mx/"), OpcionTienda("Amazon", 19999.0, 4.6, "https://www.amazon.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1577979749830-f1d742b96791?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Tv),
    ProductoUi(14, "Hisense U8K Mini-LED", CategoriaType.TV, "Mejor calidad-precio 4K 144Hz", listOf(OpcionTienda("Amazon", 14000.0, 4.5, "https://www.amazon.com.mx/"), OpcionTienda("Walmart", 13500.0, 4.4, "https://www.walmart.com.mx/")), imagenUrl = "https://images.unsplash.com/photo-1552975084-6e027cd345c2?q=80&w=1000&auto=format&fit=crop", iconoDefault = Icons.Default.Tv)
)

// ==========================================
// 2. VIEWMODEL (CON LÓGICA DE FAVORITOS)
// ==========================================
class MainViewModel : ViewModel() {
    private val _usuarioLogueado = MutableStateFlow<String?>(null)
    val usuarioLogueado = _usuarioLogueado.asStateFlow()
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda = _textoBusqueda.asStateFlow()
    private val _categoriaSeleccionada = MutableStateFlow(CategoriaType.TODOS)
    val categoriaSeleccionada = _categoriaSeleccionada.asStateFlow()

    // Usamos una lista mutable "viva" para poder actualizar favoritos y scraping
    private val _catalogoVivo = MutableStateFlow(catalogoCompleto)

    // Esta lista combina búsqueda, categoría y cambios en el catálogo (favoritos/precios)
    val productosVisibles = combine(_textoBusqueda, _categoriaSeleccionada, _catalogoVivo) { texto, categoria, catalogo ->
        catalogo.filter { producto ->
            val pasaCategoria = categoria == CategoriaType.TODOS || producto.categoria == categoria
            val pasaTexto = texto.isEmpty() || producto.nombre.contains(texto, ignoreCase = true)
            pasaCategoria && pasaTexto
        }
    }

    private val scraper = WebScraper()

    fun login(user: String) { _usuarioLogueado.value = user }
    fun logout() { _usuarioLogueado.value = null }
    fun onBusquedaChange(nuevoTexto: String) { _textoBusqueda.value = nuevoTexto }
    fun onCategoriaChange(nuevaCategoria: CategoriaType) {
        if (_categoriaSeleccionada.value == nuevaCategoria) _categoriaSeleccionada.value = CategoriaType.TODOS
        else _categoriaSeleccionada.value = nuevaCategoria
    }

    // --- NUEVA FUNCIÓN: ALTERNAR FAVORITO ---
    fun toggleFavorito(productoId: Int) {
        val listaActual = _catalogoVivo.value.toMutableList()
        // Buscamos el índice del producto en la lista viva
        val indice = listaActual.indexOfFirst { it.id == productoId }

        if (indice != -1) {
            val productoViejo = listaActual[indice]
            // Creamos una copia invirtiendo el valor de 'esFavorito'
            listaActual[indice] = productoViejo.copy(esFavorito = !productoViejo.esFavorito)
            // Actualizamos la lista, lo que repintará la UI automáticamente
            _catalogoVivo.value = listaActual
        }
    }

    fun actualizarPreciosReales(nombreProducto: String) {
        viewModelScope.launch {
            val resultado = scraper.buscarEnMercadoLibre(nombreProducto)
            if (resultado != null) {
                val listaActual = _catalogoVivo.value.toMutableList()
                val indice = listaActual.indexOfFirst { it.nombre.contains(nombreProducto, ignoreCase = true) }
                if (indice != -1) {
                    val productoViejo = listaActual[indice]
                    // Evitamos duplicar la opción "En Vivo" si ya existe
                    if (productoViejo.opciones.none { it.nombreTienda == "Mercado Libre (En Vivo)" }) {
                        val nuevaOpcion = OpcionTienda("Mercado Libre (En Vivo)", resultado.precio, 4.5, resultado.link)
                        listaActual[indice] = productoViejo.copy(opciones = productoViejo.opciones + nuevaOpcion)
                        _catalogoVivo.value = listaActual
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. NAVEGACIÓN
// ==========================================
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { PantallaPrincipal(navController, viewModel) }
        composable("login") { PantallaLogin(navController, viewModel, dbHelper) }
        composable("registro") { PantallaRegistro(navController, viewModel, dbHelper) }
        composable("favoritos") { PantallaFavoritos(navController, viewModel) } // <--- Pasamos ViewModel
        composable("comparar") { PantallaComparar(navController, viewModel) } // <--- Pasamos ViewModel para datos vivos
        composable("detalle/{productoId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productoId")?.toIntOrNull()
            PantallaDetalle(navController, id, viewModel)
        }
    }
}

// ==========================================
// 4. PANTALLAS PRINCIPALES
// ==========================================

@Composable
fun PantallaPrincipal(navController: NavController, viewModel: MainViewModel) {
    val usuario by viewModel.usuarioLogueado.collectAsState()
    // Escuchamos la lista filtrada y viva del ViewModel
    val productos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val catSeleccionada by viewModel.categoriaSeleccionada.collectAsState()

    Scaffold(bottomBar = { BottomNavigationBar(navController, "home") }, containerColor = TechBackground) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            item(span = { GridItemSpan(2) }) { HeaderSeccion(usuario, navController, viewModel) }
            item(span = { GridItemSpan(2) }) { BuscadorFuncional(textoBusqueda) { viewModel.onBusquedaChange(it) } }
            item(span = { GridItemSpan(2) }) { CategoriasFuncionales(catSeleccionada) { viewModel.onCategoriaChange(it) } }
            item(span = { GridItemSpan(2) }) {
                val titulo = if (catSeleccionada == CategoriaType.TODOS) "TENDENCIAS" else "CATEGORÍA: ${catSeleccionada.name}"
                Text(titulo, color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
            if (productos.isEmpty()) {
                item(span = { GridItemSpan(2) }) { Text("No hay resultados :(", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp)) }
            } else {
                items(productos) { producto ->
                    // Pasamos la función para dar like al hacer clic en el corazón
                    CardProductoVisual(
                        producto = producto,
                        onClick = { navController.navigate("detalle/${producto.id}") },
                        onFavoritoClick = { viewModel.toggleFavorito(producto.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaDetalle(navController: NavController, productoId: Int?, viewModel: MainViewModel) {
    val productosVivos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    // Buscamos siempre en la lista viva para ver el estado actualizado de favorito y precio
    val producto = productosVivos.find { it.id == productoId } ?: catalogoCompleto.find { it.id == productoId } ?: catalogoCompleto[0]

    LaunchedEffect(producto.id) {
        val yaTieneEnVivo = producto.opciones.any { it.nombreTienda.contains("En Vivo") }
        if (!yaTieneEnVivo) viewModel.actualizarPreciosReales(producto.nombre)
    }
    val mejorPrecio = producto.obtenerMejorPrecio()

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(White).padding(20.dp), contentAlignment = Alignment.Center) {
            ImagenProductoGrande(producto)
            // Botón Atrás
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.1f), CircleShape)) { Icon(Icons.Default.ArrowBack, null, tint = Color.Black) }

            // --- NUEVO: Botón Favorito Gigante en Detalle ---
            IconButton(
                onClick = { viewModel.toggleFavorito(producto.id) },
                modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    // Usamos iconos diferentes si está lleno o vacío
                    imageVector = if (producto.esFavorito) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (producto.esFavorito) NeonGreen else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(producto.nombre, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.background(NeonGreen, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    val promedio = if (producto.opciones.isNotEmpty()) producto.opciones.map { it.rating }.average() else 0.0
                    Text(String.format("%.1f", promedio), fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(producto.specs, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text("TIENDAS WEB (Toca para ir)", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            val opcionesOrdenadas = producto.opciones.sortedBy { it.precio }
            if (opcionesOrdenadas.isEmpty()) Text("Buscando precios...", color = Color.Gray) else opcionesOrdenadas.forEach { opcion -> FilaTiendaClickeable(opcion, opcion.precio > 0 && opcion.precio == mejorPrecio) }
        }
    }
}

// --- PANTALLA COMPARAR DINÁMICA ---
@Composable
fun PantallaComparar(navController: NavController, viewModel: MainViewModel) {
    // Usamos la lista viva del ViewModel para seleccionar
    val productosVivos by viewModel.productosVisibles.collectAsState(initial = emptyList())

    var producto1 by remember { mutableStateOf<ProductoUi?>(null) }
    var producto2 by remember { mutableStateOf<ProductoUi?>(null) }
    var mostrandoSelector by remember { mutableStateOf(false) }
    var slotSeleccionado by remember { mutableStateOf(0) }

    Scaffold(bottomBar = { BottomNavigationBar(navController, "comparar") }, containerColor = TechBackground) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("COMPARADOR", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clickable { slotSeleccionado = 1; mostrandoSelector = true }) { SelectorProductoDinamico(producto1) }
                Text("VS", color = NeonGreen, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Box(modifier = Modifier.clickable { slotSeleccionado = 2; mostrandoSelector = true }) { SelectorProductoDinamico(producto2) }
            }
            Spacer(modifier = Modifier.height(40.dp))

            // Tabla dinámica: solo se muestra si hay dos productos seleccionados
            if (producto1 != null && producto2 != null) {
                Card(colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RowTabla("Producto", producto1!!.nombre, producto2!!.nombre); Divider()
                        // Muestra el mejor precio disponible de cada uno
                        RowTabla("Mejor Precio", "$${producto1!!.obtenerMejorPrecio()}", "$${producto2!!.obtenerMejorPrecio()}"); Divider()
                        RowTabla("Categoría", producto1!!.categoria.name, producto2!!.categoria.name); Divider()
                        // Muestra las especificaciones técnicas
                        RowTabla("Specs", producto1!!.specs, producto2!!.specs)
                    }
                }
            } else {
                Text("Toca los recuadros para elegir productos", color = Color.Gray, textAlign = TextAlign.Center)
            }
        }

        // Diálogo de selección usando la lista VIVA (más de 15 productos)
        if (mostrandoSelector) {
            Dialog(onDismissRequest = { mostrandoSelector = false }) {
                Card(modifier = Modifier.fillMaxWidth().height(500.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Elige un producto", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn {
                            items(productosVivos) { prod ->
                                Row(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                                    if (slotSeleccionado == 1) producto1 = prod else producto2 = prod
                                    mostrandoSelector = false
                                }, verticalAlignment = Alignment.CenterVertically) {
                                    if(prod.imagenUrl != null) AsyncImage(model = prod.imagenUrl, contentDescription = null, modifier = Modifier.size(40.dp), contentScale = ContentScale.Fit) else Icon(prod.iconoDefault, null, tint = TechBackground, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(prod.nombre, color = Color.Black, maxLines = 1)
                                }
                                Divider(color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA FAVORITOS FUNCIONAL ---
@Composable
fun PantallaFavoritos(navController: NavController, viewModel: MainViewModel) {
    // Escuchamos la lista VIVA del ViewModel
    val productosVivos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    // Filtramos SOLO los que tienen esFavorito = true
    val favoritos = productosVivos.filter { it.esFavorito }

    Scaffold(bottomBar = { BottomNavigationBar(navController, "favoritos") }, containerColor = TechBackground) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("TUS FAVORITOS", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

            if (favoritos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes favoritos :(", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoritos) { producto ->
                        // Reutilizamos la misma tarjeta, pasando la acción de toggle
                        CardProductoVisual(
                            producto = producto,
                            onClick = { navController.navigate("detalle/${producto.id}") },
                            onFavoritoClick = { viewModel.toggleFavorito(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. COMPONENTES VISUALES Y HELPERS
// ==========================================

fun esPasswordSegura(password: String): Boolean = password.any { it.isUpperCase() } && password.any { it.isDigit() } && password.length >= 8

@Composable
fun ImagenProductoGrande(producto: ProductoUi) {
    if (producto.imagenUrl != null) {
        AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.size(250.dp), contentScale = ContentScale.Fit, error = painterResource(R.drawable.logosinfondo))
    } else {
        Icon(producto.iconoDefault, null, tint = Color.Black, modifier = Modifier.size(150.dp))
    }
}

// --- TARJETA DE PRODUCTO CON BOTÓN DE CORAZÓN ---
@Composable
fun CardProductoVisual(producto: ProductoUi, onClick: () -> Unit, onFavoritoClick: () -> Unit) {
    val mejorPrecio = producto.obtenerMejorPrecio()
    Card(colors = CardDefaults.cardColors(containerColor = White), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onClick() }) {
        Column {
            // Usamos un Box para poder poner el icono del corazón encima de la imagen
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(GrayPlaceholder)) {
                // Imagen centrada
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (producto.imagenUrl != null) {
                        AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.padding(10.dp).fillMaxSize(), contentScale = ContentScale.Fit, error = painterResource(R.drawable.logosinfondo))
                    } else {
                        Icon(producto.iconoDefault, null, tint = Color.Gray, modifier = Modifier.size(50.dp))
                    }
                }

                // Botón de Corazón en la esquina superior derecha
                IconButton(
                    onClick = onFavoritoClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.White.copy(alpha = 0.7f), CircleShape).size(32.dp)
                ) {
                    Icon(
                        // Cambia el icono entre relleno (Favorite) y borde (FavoriteBorder)
                        imageVector = if (producto.esFavorito) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        // Cambia el color entre Verde Neón y Gris
                        tint = if (producto.esFavorito) NeonGreen else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black, maxLines = 1)
                Text("${producto.opciones.size} Tiendas", fontSize = 11.sp, color = Color.Gray)
                Text("Desde $$mejorPrecio", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = TechBackground)
            }
        }
    }
}

@Composable
fun FilaTiendaClickeable(opcion: OpcionTienda, esMejorOpcion: Boolean) {
    val context = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = White), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(if (esMejorOpcion) 3.dp else 0.dp, if (esMejorOpcion) NeonGreen else Color.Transparent, RoundedCornerShape(12.dp)).clickable {
        try { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opcion.urlEnlace)); context.startActivity(intent) } catch (e: Exception) { Toast.makeText(context, "No se pudo abrir", Toast.LENGTH_SHORT).show() }
    }, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text(opcion.nombreTienda, fontWeight = FontWeight.Bold, color = Color.Black); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Star, null, tint = GoldStar, modifier = Modifier.size(14.dp)); Text(" ${opcion.rating}", fontSize = 12.sp, color = Color.Gray) }; if(esMejorOpcion) Text("¡MEJOR OPCIÓN!", color = TechBackground, fontSize = 10.sp, fontWeight = FontWeight.Black) }
            Text("$${opcion.precio}", fontWeight = FontWeight.ExtraBold, color = TechBackground, fontSize = 16.sp)
        }
    }
}

@Composable
fun BuscadorFuncional(texto: String, onChange: (String) -> Unit) {
    TextField(value = texto, onValueChange = onChange, placeholder = { Text("BUSCAR...", color = Color.Gray, fontWeight = FontWeight.Bold) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Black) }, trailingIcon = { if(texto.isNotEmpty()) IconButton(onClick = { onChange("") }) { Icon(Icons.Default.Close, null, tint = Color.Gray) } }, colors = TextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black), shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray, RoundedCornerShape(50)), singleLine = true)
}

@Composable
fun CategoriasFuncionales(seleccionada: CategoriaType, onSelect: (CategoriaType) -> Unit) {
    val categorias = listOf(Triple(Icons.Default.Smartphone, "MÓVILES", CategoriaType.MOVILES), Triple(Icons.Default.Gamepad, "GAMING", CategoriaType.GAMING), Triple(Icons.Default.Headphones, "AUDIO", CategoriaType.AUDIO), Triple(Icons.Default.Tv, "TV'S", CategoriaType.TV))
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        categorias.forEach { (icon, label, type) -> val isSelected = seleccionada == type; Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelect(type) }) { Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected) NeonGreen else White), modifier = Modifier.size(65.dp).border(if(isSelected) 2.dp else 0.dp, White, RoundedCornerShape(12.dp))) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(icon, null, tint = if (isSelected) Color.Black else TechBackground, modifier = Modifier.size(35.dp)) } }; Text(label, color = if(isSelected) NeonGreen else White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp)) } }
    }
}

@Composable
fun CampoTextoBlanco(value: String, onValueChange: (String) -> Unit, placeholder: String, isPassword: Boolean = false, keyboardType: KeyboardType = KeyboardType.Text) {
    var passwordVisible by remember { mutableStateOf(false) }
    TextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.LightGray) }, visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), trailingIcon = { if (isPassword) { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray) } } }, colors = TextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black), shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth())
}

@Composable
fun SectionRedesSociales(viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(20.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Divider(color = Color.Gray, modifier = Modifier.weight(1f)); Text("  ó  ", color = Color.Gray); Divider(color = Color.Gray, modifier = Modifier.weight(1f)) }; Spacer(modifier = Modifier.height(20.dp))
    Button(onClick = { viewModel.login("Usuario Google"); navController.navigate("home") { popUpTo("home") { inclusive = true } }; Toast.makeText(context, "Autenticado con Google", Toast.LENGTH_SHORT).show() }, colors = ButtonDefaults.buttonColors(containerColor = White), modifier = Modifier.fillMaxWidth().height(50.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(20.dp).background(Color.Red, CircleShape), contentAlignment = Alignment.Center) { Text("G", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }; Spacer(modifier = Modifier.width(10.dp)); Text("CONTINUA CON GOOGLE", color = Color.Black, fontWeight = FontWeight.Bold) } }
    Spacer(modifier = Modifier.height(10.dp))
    Button(onClick = { viewModel.login("Usuario Outlook"); navController.navigate("home") { popUpTo("home") { inclusive = true } }; Toast.makeText(context, "Autenticado con Outlook", Toast.LENGTH_SHORT).show() }, colors = ButtonDefaults.buttonColors(containerColor = White), modifier = Modifier.fillMaxWidth().height(50.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(20.dp).background(Color(0xFF0072C6), CircleShape), contentAlignment = Alignment.Center) { Text("O", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }; Spacer(modifier = Modifier.width(10.dp)); Text("CONTINUA CON OUTLOOK", color = Color.Black, fontWeight = FontWeight.Bold) } }
}

@Composable
fun SelectorProductoDinamico(producto: ProductoUi?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp).background(White, RoundedCornerShape(12.dp)).border(1.dp, NeonGreen, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            if (producto != null) {
                if (producto.imagenUrl != null) AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.padding(5.dp), contentScale = ContentScale.Fit) else Icon(producto.iconoDefault, null, tint = Color.Black, modifier = Modifier.size(40.dp))
            } else {
                Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(producto?.nombre ?: "Seleccionar", color = White, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.width(90.dp), maxLines = 1)
    }
}

@Composable
fun RowTabla(label: String, val1: String, val2: String) { Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(val1, fontWeight = FontWeight.Bold, color = TechBackground, modifier = Modifier.weight(1f), textAlign = TextAlign.Center); Text(label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center); Text(val2, fontWeight = FontWeight.Bold, color = TechBackground, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) } }

@Composable
fun BottomNavigationBar(navController: NavController, pantallaActual: String) { NavigationBar(containerColor = TechBackground, contentColor = White) { NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") }, selected = pantallaActual == "home", onClick = { navController.navigate("home") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White, indicatorColor = Color.Transparent)); NavigationBarItem(icon = { Icon(Icons.Default.CompareArrows, null) }, label = { Text("Comparar") }, selected = pantallaActual == "comparar", onClick = { navController.navigate("comparar") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White, indicatorColor = Color.Transparent)); NavigationBarItem(icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text("Favoritos") }, selected = pantallaActual == "favoritos", onClick = { navController.navigate("favoritos") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White, indicatorColor = Color.Transparent)) } }

@Composable
fun HeaderSeccion(usuario: String?, navController: NavController, viewModel: MainViewModel) { Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("DOWN WITH\nYOUR TECH", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 22.sp); if (usuario == null) { Column(horizontalAlignment = Alignment.End) { Button(onClick = { navController.navigate("registro") }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("REGÍSTRATE", color = Color.Black, fontSize = 10.sp) }; Spacer(modifier = Modifier.height(4.dp)); Button(onClick = { navController.navigate("login") }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("INICIA SESIÓN", color = Color.Black, fontSize = 10.sp) } } } else { Column(horizontalAlignment = Alignment.End) { Text(usuario, color = NeonGreen); Text("Salir", color = Color.Gray, modifier = Modifier.clickable { viewModel.logout() }) } } } }

// --- LOGIN & REGISTRO (SIN CAMBIOS MAYORES) ---
@Composable
fun PantallaLogin(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuarioText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(TechBackground).padding(30.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("DOWN WITH YOUR TECH", color = White, fontWeight = FontWeight.Bold, fontSize = 22.sp); Spacer(modifier = Modifier.height(20.dp))
        Image(painter = painterResource(id = R.drawable.logosinfondo), contentDescription = "Logo", modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.height(30.dp)); CampoTextoBlanco(usuarioText, { usuarioText = it }, "USUARIO/CORREO"); Spacer(modifier = Modifier.height(15.dp)); CampoTextoBlanco(passText, { passText = it }, "CONTRASEÑA", isPassword = true); Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { val nombreReal = dbHelper.validarLogin(usuarioText, passText); if(nombreReal != null) { viewModel.login(nombreReal); navController.popBackStack(); Toast.makeText(context, "Bienvenido $nombreReal", Toast.LENGTH_SHORT).show() } else { Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show() } }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("INICIAR SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold) }
        SectionRedesSociales(viewModel, navController)
    }
}

@Composable
fun PantallaRegistro(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passConfirm by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(TechBackground).padding(30.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("REGÍSTRATE", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(20.dp))
        Image(painter = painterResource(id = R.drawable.logosinfondo), contentDescription = "Logo", modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.height(30.dp)); CampoTextoBlanco(usuario, { usuario = it }, "USUARIO"); Spacer(modifier = Modifier.height(15.dp)); CampoTextoBlanco(correo, { correo = it }, "CORREO", keyboardType = KeyboardType.Email); Spacer(modifier = Modifier.height(15.dp)); CampoTextoBlanco(pass, { pass = it }, "CONTRASEÑA", isPassword = true); Spacer(modifier = Modifier.height(15.dp)); CampoTextoBlanco(passConfirm, { passConfirm = it }, "CONFIRMAR CONTRASEÑA", isPassword = true); Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { if (usuario.isEmpty() || correo.isEmpty() || pass.isEmpty()) { Toast.makeText(context, "Llena todo", Toast.LENGTH_SHORT).show() } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show() } else if (!esPasswordSegura(pass)) { Toast.makeText(context, "Contraseña insegura", Toast.LENGTH_LONG).show() } else if (pass != passConfirm) { Toast.makeText(context, "No coinciden", Toast.LENGTH_SHORT).show() } else { if (dbHelper.existeUsuarioOCorreo(usuario, correo)) { Toast.makeText(context, "Ya existe", Toast.LENGTH_LONG).show() } else { if (dbHelper.registrarUsuario(usuario, correo, pass)) { viewModel.login(usuario); navController.navigate("home") { popUpTo("home") { inclusive = true } }; Toast.makeText(context, "Registro Exitoso", Toast.LENGTH_SHORT).show() } } } }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("CREAR CUENTA", color = Color.Black, fontWeight = FontWeight.Bold) }
        SectionRedesSociales(viewModel, navController)
    }
}