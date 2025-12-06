package com.example.downwithyourtech.ui


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// 1. CONSTANTES DE DISEÑO (Colores y Modelos)
// ==========================================

// Colores basados en el PDF (Azul oscuro y Verde Neón)
val TechBackground = Color(0xFF000040)
val NeonGreen = Color(0xFFCCFF00)
val White = Color.White
val GrayPlaceholder = Color(0xFFEEEEEE)

// Modelo para la UI (Visual)
data class ProductoUi(
    val id: Int,
    val nombre: String,
    val precio: String,
    val tienda: String
)

// Helper falso para la Base de Datos (Si ya tienes tu archivo DatabaseHelper, borra esta clase)
class DatabaseHelper(context: android.content.Context) {
    fun validarLogin(u: String, p: String): Boolean = true // Simulado
    fun registrarUsuario(u: String, c: String, p: String): Boolean = true // Simulado
    fun obtenerNombreUsuario(u: String): String = u
}

// ==========================================
// 2. VIEWMODEL (Lógica de la pantalla)
// ==========================================

class MainViewModel : ViewModel() {
    private val _usuarioLogueado = MutableStateFlow<String?>(null)
    val usuarioLogueado = _usuarioLogueado.asStateFlow()

    fun login(user: String) { _usuarioLogueado.value = user }
    fun logout() { _usuarioLogueado.value = null }
}

// ==========================================
// 3. NAVEGACIÓN PRINCIPAL
// ==========================================

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    NavHost(navController = navController, startDestination = "home") {
        // PANTALLA PRINCIPAL (DISEÑO TIPO PDF)
        composable("home") {
            PantallaPrincipal(navController, viewModel)
        }
        // PANTALLA LOGIN
        composable("login") {
            PantallaLogin(navController, viewModel, dbHelper)
        }
        // PANTALLA REGISTRO
        composable("registro") {
            PantallaRegistro(navController, viewModel, dbHelper)
        }
    }
}

// ==========================================
// 4. PANTALLAS (UI)
// ==========================================

// --- HOME (GRID Y ESTILO REVISTA) ---
@Composable
fun PantallaPrincipal(navController: NavController, viewModel: MainViewModel) {
    val usuario by viewModel.usuarioLogueado.collectAsState()

    // Datos falsos para que se vea bonito el diseño
    val productosTendencia = listOf(
        ProductoUi(1, "RTX 4060", "$6,500", "Amazon"),
        ProductoUi(2, "Galaxy Buds", "$2,200", "Samsung"),
        ProductoUi(3, "iPhone 15", "$18,000", "Apple"),
        ProductoUi(4, "Logitech G", "$800", "Cyberpuerta"),
        ProductoUi(5, "Redmi Note", "$4,500", "Xiaomi"),
        ProductoUi(6, "Asus TUF", "$22,000", "Liverpool")
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }, // Barra inferior
        containerColor = TechBackground // Fondo Azul Oscuro
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Columnas
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // HEADER (Ocupa las 2 columnas)
            item(span = { GridItemSpan(2) }) {
                HeaderSeccion(usuario, navController, viewModel)
            }

            // BUSCADOR (Ocupa las 2 columnas)
            item(span = { GridItemSpan(2) }) {
                BuscadorVisual()
            }

            // CATEGORÍAS (Ocupa las 2 columnas)
            item(span = { GridItemSpan(2) }) {
                CategoriasVisuales()
            }

            // TÍTULO TENDENCIAS
            item(span = { GridItemSpan(2) }) {
                Text(
                    "TENDENCIAS",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // GRID DE PRODUCTOS (1 columna cada uno)
            items(productosTendencia) { producto ->
                CardProductoVisual(producto) {
                    // Acción al hacer click (ej. ir a detalle)
                }
            }
        }
    }
}

// --- LOGIN (ESTILIZADO) ---
@Composable
fun PantallaLogin(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuarioText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(TechBackground).padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DOWN WITH YOUR TECH", color = White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Icon(Icons.Default.Power, null, tint = White, modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(30.dp))

        CampoTextoBlanco(usuarioText, { usuarioText = it }, "USUARIO/CORREO")
        Spacer(modifier = Modifier.height(15.dp))
        CampoTextoBlanco(passText, { passText = it }, "CONTRASEÑA", isPassword = true)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if(dbHelper.validarLogin(usuarioText, passText)) {
                    viewModel.login(usuarioText)
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("INICIAR SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// --- REGISTRO (ESTILIZADO) ---
@Composable
fun PantallaRegistro(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuario by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(TechBackground).padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("REGÍSTRATE", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        CampoTextoBlanco(usuario, { usuario = it }, "USUARIO")
        Spacer(modifier = Modifier.height(15.dp))
        CampoTextoBlanco(pass, { pass = it }, "CONTRASEÑA", isPassword = true)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                dbHelper.registrarUsuario(usuario, "", pass)
                viewModel.login(usuario)
                navController.navigate("home") { popUpTo("home") { inclusive = true } }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("CREAR CUENTA", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 5. COMPONENTES VISUALES (TARJETAS, BOTONES)
// ==========================================

@Composable
fun HeaderSeccion(usuario: String?, navController: NavController, viewModel: MainViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("DOWN WITH\nYOUR TECH", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 22.sp)

        if (usuario == null) {
            Column(horizontalAlignment = Alignment.End) {
                BotonVerdePequeno("REGÍSTRATE") { navController.navigate("registro") }
                Spacer(modifier = Modifier.height(4.dp))
                BotonVerdePequeno("INICIA SESIÓN") { navController.navigate("login") }
            }
        } else {
            Column(horizontalAlignment = Alignment.End) {
                Icon(Icons.Default.AccountCircle, null, tint = NeonGreen)
                Text(usuario, color = White, fontSize = 12.sp)
                Text("Salir", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.clickable { viewModel.logout() })
            }
        }
    }
}

@Composable
fun CategoriasVisuales() {
    val categorias = listOf(
        Icons.Default.Smartphone to "MÓVILES",
        Icons.Default.Gamepad to "GAMING",
        Icons.Default.Headphones to "AUDIO",
        Icons.Default.Tv to "TV'S"
    )
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        categorias.forEach { (icon, label) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = White), modifier = Modifier.size(65.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = TechBackground, modifier = Modifier.size(35.dp))
                    }
                }
                Text(label, color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun CardProductoVisual(producto: ProductoUi, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp).clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(GrayPlaceholder), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, maxLines = 1)
                Text(producto.tienda, fontSize = 10.sp, color = Color.Gray)
                Text(producto.precio, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = TechBackground)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = TechBackground, contentColor = White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") },
            selected = true,
            onClick = { navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CompareArrows, null) },
            label = { Text("Comparar") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = White, unselectedTextColor = White)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, null) },
            label = { Text("Favoritos") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = White, unselectedTextColor = White)
        )
    }
}

@Composable
fun BuscadorVisual() {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(White).padding(12.dp)) {
        Row {
            Text("BUSCAR", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Search, null, tint = Color.Black)
        }
    }
}

@Composable
fun CampoTextoBlanco(value: String, onValueChange: (String) -> Unit, placeholder: String, isPassword: Boolean = false) {
    TextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.LightGray) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
        shape = RoundedCornerShape(50),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotonVerdePequeno(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.height(26.dp)) {
        Text(text, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}