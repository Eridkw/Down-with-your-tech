package com.example.downwithyourtech.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.style.TextOverflow
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
// 1. CONSTANTES Y ESTILOS
// ==========================================
val TechBackground = Color(0xFF0E0E2C)
val BottomNavBackground = Color(0xFF000025)
val NeonGreen = Color(0xFF37FF00)
val White = Color.White
val CardBackground = Color(0xFFF0F2F5)
val GrayText = Color(0xFF6C757D)
val GoldStar = Color(0xFFFFD700)

val CardShape = RoundedCornerShape(20.dp)
val ButtonShape = RoundedCornerShape(30.dp)

enum class CategoriaType { TODOS, MOVILES, GAMING, AUDIO, TV, COMPUTO }

enum class Moneda(val simbolo: String, val factor: Double, val codigo: String) {
    MXN("$", 1.0, "MXN"),
    USD("$", 0.05, "USD"),
    EUR("€", 0.045, "EUR")
}

enum class Idioma(val nombre: String) {
    ESP("Español"),
    ENG("English"),
    CHI("中文")
}

// --- DICCIONARIO DE TRADUCCIÓN ---
object Diccionario {
    val textos = mapOf(
        Idioma.ESP to mapOf(
            "inicio" to "Inicio", "comparar" to "Comparar", "favoritos" to "Favoritos",
            "buscar" to "Buscar...", "tendencias" to "TENDENCIAS", "sin_resultados" to "Sin resultados",
            "entrar" to "Entrar", "registrate" to "REGÍSTRATE", "inicia_sesion" to "INICIA SESIÓN",
            "salir" to "Salir", "perfil" to "Perfil", "ajustes" to "Ajustes", "guia" to "Guía de Tiendas",
            "cerrar_sesion" to "CERRAR SESIÓN", "datos_cuenta" to "DATOS DE CUENTA",
            "vincular_servicios" to "VINCULAR SERVICIOS", "vincular_amazon" to "Vincular Amazon",
            "vincular_ebay" to "Vincular eBay", "vincular_meli" to "Vincular MercadoLibre",
            "general" to "GENERAL", "idioma" to "Idioma", "divisas" to "Divisas",
            "notificaciones" to "NOTIFICACIONES", "bajada_precio" to "Bajada de Precio",
            "temp_ofertas" to "Temporada de Ofertas", "stock" to "Stock Disponible",
            "legal" to "LEGAL", "terminos" to "Términos y Condiciones",
            "comparador" to "COMPARADOR", "vs" to "VS", "elige" to "Elige un producto",
            "sugeridos" to "Sugeridos", "precio" to "PRECIO", "calificacion" to "CALIFICACIÓN",
            "disponible" to "DISPONIBLE EN", "specs" to "SPECS", "donde_comprar" to "DÓNDE COMPRAR",
            "crear_cuenta" to "Crear Cuenta", "unete" to "Únete a la comunidad tech",
            "usuario" to "Usuario", "correo" to "Correo Electrónico", "pass" to "Contraseña",
            "pass_conf" to "Confirmar Contraseña", "bienvenido" to "Bienvenido de nuevo",
            "continuar" to "Inicia sesión para continuar", "usuario_correo" to "Usuario o Correo",
            "no_cuenta" to "¿No tienes cuenta? ", "ya_cuenta" to "¿Ya tienes cuenta? "
        ),
        Idioma.ENG to mapOf(
            "inicio" to "Home", "comparar" to "Compare", "favoritos" to "Favorites",
            "buscar" to "Search...", "tendencias" to "TRENDING", "sin_resultados" to "No results",
            "entrar" to "Login", "registrate" to "REGISTER", "inicia_sesion" to "LOG IN",
            "salir" to "Log out", "perfil" to "Profile", "ajustes" to "Settings", "guia" to "Store Guide",
            "cerrar_sesion" to "LOG OUT", "datos_cuenta" to "ACCOUNT DATA",
            "vincular_servicios" to "LINK SERVICES", "vincular_amazon" to "Link Amazon",
            "vincular_ebay" to "Link eBay", "vincular_meli" to "Link MercadoLibre",
            "general" to "GENERAL", "idioma" to "Language", "divisas" to "Currency",
            "notificaciones" to "NOTIFICATIONS", "bajada_precio" to "Price Drop",
            "temp_ofertas" to "Sales Season", "stock" to "Stock Available",
            "legal" to "LEGAL", "terminos" to "Terms & Conditions",
            "comparador" to "COMPARATOR", "vs" to "VS", "elige" to "Choose a product",
            "sugeridos" to "Suggested", "precio" to "PRICE", "calificacion" to "RATING",
            "disponible" to "AVAILABLE AT", "specs" to "SPECS", "donde_comprar" to "WHERE TO BUY",
            "crear_cuenta" to "Create Account", "unete" to "Join the tech community",
            "usuario" to "Username", "correo" to "Email", "pass" to "Password",
            "pass_conf" to "Confirm Password", "bienvenido" to "Welcome back",
            "continuar" to "Log in to continue", "usuario_correo" to "User or Email",
            "no_cuenta" to "No account? ", "ya_cuenta" to "Already have an account? "
        ),
        Idioma.CHI to mapOf(
            "inicio" to "主页", "comparar" to "比较", "favoritos" to "收藏夹",
            "buscar" to "搜索...", "tendencias" to "趋势", "sin_resultados" to "无结果",
            "entrar" to "登录", "registrate" to "注册", "inicia_sesion" to "登录",
            "salir" to "退出", "perfil" to "个人资料", "ajustes" to "设置", "guia" to "商店指南",
            "cerrar_sesion" to "注销", "datos_cuenta" to "账户数据",
            "vincular_servicios" to "关联服务", "vincular_amazon" to "关联亚马逊",
            "vincular_ebay" to "关联 eBay", "vincular_meli" to "关联美卡多",
            "general" to "常规", "idioma" to "语言", "divisas" to "货币",
            "notificaciones" to "通知", "bajada_precio" to "降价",
            "temp_ofertas" to "促销季", "stock" to "库存可用",
            "legal" to "法律", "terminos" to "条款和条件",
            "comparador" to "比较器", "vs" to "对", "elige" to "选择产品",
            "sugeridos" to "建议", "precio" to "价格", "calificacion" to "评分",
            "disponible" to "可用於", "specs" to "规格", "donde_comprar" to "去哪买",
            "crear_cuenta" to "创建账户", "unete" to "加入科技社区",
            "usuario" to "用户名", "correo" to "电子邮件", "pass" to "密码",
            "pass_conf" to "确认密码", "bienvenido" to "欢迎回来",
            "continuar" to "登录以继续", "usuario_correo" to "用户或电子邮件",
            "no_cuenta" to "没有账户？", "ya_cuenta" to "已有账户？"
        )
    )
}

data class OpcionTienda(val nombreTienda: String, val precio: Double, val rating: Double, val urlEnlace: String)

data class ProductoUi(
    val id: Int, val nombre: String, val categoria: CategoriaType, val specs: String,
    val opciones: List<OpcionTienda>, val imagenUrl: String? = null,
    val iconoDefault: ImageVector = Icons.Default.Devices, val esFavorito: Boolean = false
) {
    fun obtenerMejorPrecio(): Double = opciones.minOfOrNull { it.precio } ?: 0.0
}

// --- DATOS EXPANDIDOS ---
val catalogoCompleto = listOf(
    // GAMING
    ProductoUi(1, "RTX 4060 8GB", CategoriaType.GAMING, "8GB GDDR6, DLSS 3.0", listOf(OpcionTienda("Amazon", 6500.0, 4.8, "https://www.amazon.com.mx/s?k=rtx+4060"), OpcionTienda("MercadoLibre", 6300.0, 4.7, "https://listado.mercadolibre.com.mx/rtx-4060")), imagenUrl = "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=1000", iconoDefault = Icons.Default.Computer),
    ProductoUi(5, "Logitech G502 X", CategoriaType.GAMING, "Hero 25K, Híbrido", listOf(OpcionTienda("Amazon", 1500.0, 4.8, "https://www.amazon.com.mx/s?k=logitech+g502x"), OpcionTienda("Cyberpuerta", 1450.0, 4.5, "https://www.cyberpuerta.mx/Mas/Buscar/?q=logitech+g502x")), imagenUrl = "https://images.unsplash.com/photo-1615663245857-acda6b247195?q=80&w=1000", iconoDefault = Icons.Default.Mouse),
    ProductoUi(6, "Teclado Mecánico 60%", CategoriaType.GAMING, "Red Switches, RGB", listOf(OpcionTienda("Amazon", 800.0, 4.4, "https://www.amazon.com.mx/s?k=teclado+mecanico+60"), OpcionTienda("AliExpress", 600.0, 4.2, "https://es.aliexpress.com/w/wholesale-mechanical-keyboard.html")), imagenUrl = "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=1000", iconoDefault = Icons.Default.Keyboard),
    ProductoUi(15, "Steam Deck OLED", CategoriaType.GAMING, "512GB, OLED HDR", listOf(OpcionTienda("Amazon", 14000.0, 4.9, "https://www.amazon.com.mx/s?k=steam+deck+oled"), OpcionTienda("MercadoLibre", 13500.0, 4.8, "https://listado.mercadolibre.com.mx/steam-deck")), imagenUrl = "https://images.unsplash.com/photo-1698668687273-61d373594124?q=80&w=1000", iconoDefault = Icons.Default.Gamepad),
    // COMPUTO
    ProductoUi(16, "AMD Ryzen 7 5700X", CategoriaType.COMPUTO, "8 Núcleos, 4.6GHz", listOf(OpcionTienda("Amazon", 3200.0, 4.8, "https://www.amazon.com.mx/s?k=ryzen+7+5700x"), OpcionTienda("Cyberpuerta", 2999.0, 4.7, "https://www.cyberpuerta.mx/Mas/Buscar/?q=ryzen+7+5700x")), imagenUrl = "https://images.unsplash.com/photo-1555618254-76a8a9a9108a?q=80&w=1000", iconoDefault = Icons.Default.Memory),
    ProductoUi(17, "RAM Corsair 32GB", CategoriaType.COMPUTO, "DDR4 3200MHz", listOf(OpcionTienda("Amazon", 1400.0, 4.8, "https://www.amazon.com.mx/s?k=corsair+vengeance+32gb"), OpcionTienda("MercadoLibre", 1500.0, 4.7, "https://listado.mercadolibre.com.mx/ram-32gb")), imagenUrl = "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=1000", iconoDefault = Icons.Default.Memory),
    ProductoUi(18, "Samsung 980 PRO 1TB", CategoriaType.COMPUTO, "SSD NVMe M.2", listOf(OpcionTienda("Amazon", 1800.0, 4.9, "https://www.amazon.com.mx/s?k=samsung+980+pro"), OpcionTienda("DD Tech", 1750.0, 4.8, "https://ddtech.mx/buscar/samsung+980+pro")), imagenUrl = "https://images.unsplash.com/photo-1628557044797-f21a177c37ec?q=80&w=1000", iconoDefault = Icons.Default.Settings),
    ProductoUi(19, "MacBook Air M2", CategoriaType.COMPUTO, "Chip M2, 13.6 Retina", listOf(OpcionTienda("Apple", 22999.0, 5.0, "https://www.apple.com/mx/macbook-air/"), OpcionTienda("Amazon", 19500.0, 4.7, "https://www.amazon.com.mx/s?k=macbook+air+m2")), imagenUrl = "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?q=80&w=1000", iconoDefault = Icons.Default.Laptop),
    // AUDIO
    ProductoUi(2, "Galaxy Buds 3 Pro", CategoriaType.AUDIO, "ANC, 24bit Hi-Fi", listOf(OpcionTienda("Samsung", 3200.0, 4.9, "https://www.samsung.com/mx/search/?searchvalue=buds"), OpcionTienda("Amazon", 2900.0, 4.7, "https://www.amazon.com.mx/s?k=galaxy+buds+3+pro")), imagenUrl = "https://images.unsplash.com/photo-1610427303867-b703e72dc07e?q=80&w=1000", iconoDefault = Icons.Default.Headphones),
    ProductoUi(8, "Sony WH-1000XM5", CategoriaType.AUDIO, "Noise Cancelling Top", listOf(OpcionTienda("Sony Store", 6500.0, 4.9, "https://store.sony.com.mx/search?q=xm5"), OpcionTienda("Amazon", 5800.0, 4.8, "https://www.amazon.com.mx/s?k=sony+wh1000xm5")), imagenUrl = "https://images.unsplash.com/photo-1613040809024-b4ef748fc483?q=80&w=1000", iconoDefault = Icons.Default.Headphones),
    // MOVILES
    ProductoUi(3, "iPhone 15 Pro", CategoriaType.MOVILES, "A17 Pro, Titanio", listOf(OpcionTienda("Apple", 23999.0, 5.0, "https://www.apple.com/mx/iphone-15-pro/"), OpcionTienda("Amazon", 22500.0, 4.8, "https://www.amazon.com.mx/s?k=iphone+15+pro")), imagenUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?q=80&w=1000", iconoDefault = Icons.Default.Smartphone),
    ProductoUi(10, "Samsung S24 Ultra", CategoriaType.MOVILES, "AI, Zoom 100x", listOf(OpcionTienda("Samsung", 26000.0, 4.9, "https://www.samsung.com/mx/smartphones/galaxy-s24-ultra/"), OpcionTienda("Amazon", 24500.0, 4.7, "https://www.amazon.com.mx/s?k=s24+ultra")), imagenUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?q=80&w=1000", iconoDefault = Icons.Default.Smartphone),
    // TV
    ProductoUi(4, "Xiaomi TV Q2 55\"", CategoriaType.TV, "QLED 4K, GoogleTV", listOf(OpcionTienda("Walmart", 8500.0, 4.4, "https://www.walmart.com.mx/buscar?q=xiaomi+tv"), OpcionTienda("Amazon", 8900.0, 4.6, "https://www.amazon.com.mx/s?k=xiaomi+tv+q2")), imagenUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?q=80&w=1000", iconoDefault = Icons.Default.Tv),
    ProductoUi(12, "LG OLED C3 65\"", CategoriaType.TV, "OLED 120Hz", listOf(OpcionTienda("Liverpool", 25000.0, 4.9, "https://www.liverpool.com.mx/buscar/lg-oled-c3"), OpcionTienda("Costco", 24500.0, 4.8, "https://www.costco.com.mx/search?text=oled")), imagenUrl = "https://images.unsplash.com/photo-1601944179066-29786cb9d32a?q=80&w=1000", iconoDefault = Icons.Default.Tv)
)

// ==========================================
// 3. VIEWMODEL
// ==========================================
class MainViewModel : ViewModel() {
    private val _usuarioLogueado = MutableStateFlow<String?>(null)
    val usuarioLogueado = _usuarioLogueado.asStateFlow()

    private val _notifPrecio = MutableStateFlow(true); val notifPrecio = _notifPrecio.asStateFlow()
    private val _notifOfertas = MutableStateFlow(false); val notifOfertas = _notifOfertas.asStateFlow()
    private val _notifStock = MutableStateFlow(true); val notifStock = _notifStock.asStateFlow()

    private val _textoBusqueda = MutableStateFlow(""); val textoBusqueda = _textoBusqueda.asStateFlow()
    private val _categoriaSeleccionada = MutableStateFlow(CategoriaType.TODOS); val categoriaSeleccionada = _categoriaSeleccionada.asStateFlow()
    private val _catalogoVivo = MutableStateFlow(catalogoCompleto)

    private val _monedaSeleccionada = MutableStateFlow(Moneda.MXN)
    val monedaSeleccionada = _monedaSeleccionada.asStateFlow()

    private val _idiomaSeleccionado = MutableStateFlow(Idioma.ESP)
    val idiomaSeleccionado = _idiomaSeleccionado.asStateFlow()

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
    fun onBusquedaChange(t: String) { _textoBusqueda.value = t }
    fun onCategoriaChange(c: CategoriaType) {
        if (_categoriaSeleccionada.value == c) _categoriaSeleccionada.value = CategoriaType.TODOS else _categoriaSeleccionada.value = c
    }

    fun toggleFavorito(id: Int, context: android.content.Context) {
        if (_usuarioLogueado.value == null) {
            Toast.makeText(context, obtenerTexto("inicia_sesion"), Toast.LENGTH_SHORT).show() // Traducción usada
            return
        }
        val lista = _catalogoVivo.value.toMutableList()
        val idx = lista.indexOfFirst { it.id == id }
        if (idx != -1) {
            lista[idx] = lista[idx].copy(esFavorito = !lista[idx].esFavorito)
            _catalogoVivo.value = lista
        }
    }

    fun toggleNotifPrecio() { _notifPrecio.value = !_notifPrecio.value }
    fun toggleNotifOfertas() { _notifOfertas.value = !_notifOfertas.value }
    fun toggleNotifStock() { _notifStock.value = !_notifStock.value }

    fun cambiarMoneda(nueva: Moneda) { _monedaSeleccionada.value = nueva }
    fun cambiarIdioma(nuevo: Idioma) { _idiomaSeleccionado.value = nuevo }

    fun convertirPrecio(precioBase: Double): String {
        val moneda = _monedaSeleccionada.value
        val precioConvertido = precioBase * moneda.factor
        return "${moneda.simbolo}${String.format("%,.2f", precioConvertido)} ${moneda.codigo}"
    }

    // Helper para obtener textos traducidos según el estado actual
    fun obtenerTexto(key: String): String {
        return Diccionario.textos[_idiomaSeleccionado.value]?.get(key) ?: key
    }

    fun actualizarPreciosReales(nombre: String) {
        viewModelScope.launch {
            val res = scraper.buscarEnMercadoLibre(nombre)
            if (res != null) {
                val list = _catalogoVivo.value.toMutableList()
                val idx = list.indexOfFirst { it.nombre.contains(nombre, true) }
                if (idx != -1 && list[idx].opciones.none { it.nombreTienda.contains("En Vivo") }) {
                    val nueva = OpcionTienda("Mercado Libre (En Vivo)", res.precio, 4.5, res.link)
                    list[idx] = list[idx].copy(opciones = list[idx].opciones + nueva)
                    _catalogoVivo.value = list
                }
            }
        }
    }
}

// ==========================================
// 4. COMPONENTES VISUALES
// ==========================================
fun esPasswordSegura(p: String) = p.length >= 4

@Composable
fun CampoTextoBlanco(value: String, onValueChange: (String) -> Unit, placeholder: String, isPassword: Boolean = false, keyboardType: KeyboardType = KeyboardType.Text) {
    var passwordVisible by remember { mutableStateOf(false) }
    TextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = GrayText) },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = { if (isPassword) IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = GrayText) } },
        colors = TextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = TechBackground, cursorColor = TechBackground),
        shape = ButtonShape, modifier = Modifier.fillMaxWidth().shadow(2.dp, ButtonShape)
    )
}

@Composable
fun ImagenProductoGrande(p: ProductoUi) {
    if (p.imagenUrl != null) { AsyncImage(model = p.imagenUrl, contentDescription = null, modifier = Modifier.size(280.dp), contentScale = ContentScale.Fit, error = painterResource(R.drawable.logosinfondo)) }
    else { Icon(p.iconoDefault, null, tint = TechBackground, modifier = Modifier.size(150.dp)) }
}

@Composable
fun CardProductoVisual(producto: ProductoUi, precioFormateado: String, onClick: () -> Unit, onFavoritoClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground), shape = CardShape, modifier = Modifier.fillMaxWidth().height(260.dp).clickable { onClick() }) {
        Column {
            Box(modifier = Modifier.weight(1.4f).fillMaxWidth().background(White), contentAlignment = Alignment.Center) {
                if(producto.imagenUrl != null) { AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.padding(10.dp).fillMaxSize(), contentScale = ContentScale.Fit, error = painterResource(R.drawable.logosinfondo)) }
                else { Icon(producto.iconoDefault, null, tint = Color.Gray, modifier = Modifier.size(60.dp)) }
                IconButton(onClick = onFavoritoClick, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(if (producto.esFavorito) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if(producto.esFavorito) NeonGreen else GrayText) }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, color = TechBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Desde $precioFormateado", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NeonGreen)
            }
        }
    }
}

// AQUÍ ESTÁ EL CAMBIO: Recibe precioFormateado en lugar de calcularlo
@Composable
fun FilaTiendaClickeable(opcion: OpcionTienda, precioFormateado: String, esMejor: Boolean) {
    val ctx = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground), border = if(esMejor) BorderStroke(2.dp, NeonGreen) else null, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { try { val i = Intent(Intent.ACTION_VIEW, Uri.parse(opcion.urlEnlace)); ctx.startActivity(i) } catch (e: Exception) { Toast.makeText(ctx, "Error link", Toast.LENGTH_SHORT).show() } }, shape = CardShape) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(opcion.nombreTienda, fontWeight = FontWeight.Bold, color = TechBackground)
            Text(precioFormateado, fontWeight = FontWeight.Black, color = TechBackground)
        }
    }
}

@Composable
fun BuscadorFuncional(texto: String, placeholder: String, onChange: (String) -> Unit) {
    TextField(value = texto, onValueChange = onChange, placeholder = { Text(placeholder, color = GrayText) }, colors = TextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White, focusedTextColor = TechBackground), shape = ButtonShape, modifier = Modifier.fillMaxWidth())
}

@Composable
fun CategoriasFuncionales(sel: CategoriaType, onSelect: (CategoriaType) -> Unit) {
    val listaCategorias = listOf(
        Triple(Icons.Default.Smartphone, "MÓVILES", CategoriaType.MOVILES),
        Triple(Icons.Default.Gamepad, "GAMING", CategoriaType.GAMING),
        Triple(Icons.Default.Headphones, "AUDIO", CategoriaType.AUDIO),
        Triple(Icons.Default.Tv, "TV", CategoriaType.TV),
        Triple(Icons.Default.Computer, "CÓMPUTO", CategoriaType.COMPUTO)
    )

    LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        items(listaCategorias) { (icon, label, type) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelect(type) }) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(if (sel == type) NeonGreen else White), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = label, tint = if (sel == type) TechBackground else GrayText, modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = label, color = if (sel == type) NeonGreen else White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SelectorProductoDinamico(p: ProductoUi?, textoElegir: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(White).border(BorderStroke(1.dp, NeonGreen), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            if (p?.imagenUrl != null) AsyncImage(model = p.imagenUrl, contentDescription = null, modifier = Modifier.padding(5.dp)) else Icon(Icons.Default.Add, null, tint = GrayText)
        }
        Text(p?.nombre ?: textoElegir, color = White, fontSize = 12.sp)
    }
}

@Composable
fun RowTabla(l: String, v1: String, v2: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(v1, color = TechBackground, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(l, color = GrayText, fontSize = 12.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.Center)
        Text(v2, color = TechBackground, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
fun BottomNavigationBar(nav: NavController, pant: String, vm: MainViewModel) {
    val idioma by vm.idiomaSeleccionado.collectAsState()

    // Función auxiliar para redibujar al cambiar idioma
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    NavigationBar(containerColor = BottomNavBackground, contentColor = White) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text(t("inicio")) }, selected = pant == "home", onClick = { nav.navigate("home") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White.copy(alpha=0.6f), indicatorColor = Color.Transparent, selectedTextColor = White, unselectedTextColor = White.copy(alpha=0.6f)))
        NavigationBarItem(icon = { Icon(Icons.Default.CompareArrows, null) }, label = { Text(t("comparar")) }, selected = pant == "comparar", onClick = { nav.navigate("comparar") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White.copy(alpha=0.6f), indicatorColor = Color.Transparent, selectedTextColor = White, unselectedTextColor = White.copy(alpha=0.6f)))
        NavigationBarItem(icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text(t("favoritos")) }, selected = pant == "favoritos", onClick = { nav.navigate("favoritos") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeonGreen, unselectedIconColor = White.copy(alpha=0.6f), indicatorColor = Color.Transparent, selectedTextColor = White, unselectedTextColor = White.copy(alpha=0.6f)))
    }
}

@Composable
fun HeaderSeccion(user: String?, nav: NavController, vm: MainViewModel, onMenuClick: () -> Unit) {
    val idioma by vm.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (user != null) { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, null, tint = NeonGreen, modifier = Modifier.size(32.dp)) }; Spacer(modifier = Modifier.width(8.dp)) }
                Column { Text("DOWN WITH", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp); Text("YOUR TECH", color = NeonGreen, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) }
            }
            if (user == null) {
                Column(horizontalAlignment = Alignment.End) {
                    Button(onClick = { nav.navigate("registro") }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text(t("registrate"), color = Color.Black, fontSize = 10.sp) }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = { nav.navigate("login") }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Text(t("inicia_sesion"), color = Color.Black, fontSize = 10.sp) }
                }
            } else { Column(horizontalAlignment = Alignment.End) { Text(user, color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp); Text(t("salir"), color = GrayText, fontSize = 10.sp, modifier = Modifier.clickable { vm.logout() }) } }
        }
        Divider(color = White.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))
    }
}

@Composable
fun ItemPerfil(texto: String, icon: ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = White, modifier = Modifier.size(24.dp)); Spacer(modifier = Modifier.width(16.dp)); Text(texto, color = White, fontSize = 16.sp, modifier = Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null, tint = GrayText) }; Divider(color = Color.White.copy(alpha = 0.1f))
}

@Composable
fun BotonVinculacion(texto: String, fondo: Color, textoColor: Color, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = fondo), shape = ButtonShape, modifier = Modifier.fillMaxWidth().height(55.dp)) { Text(texto, color = textoColor, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
fun ItemAjusteSelector(titulo: String, valorActual: String, opciones: List<String>, onSeleccion: (Int) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable { expandido = true }.padding(vertical = 16.dp).clip(CardShape).background(Color.White.copy(alpha = 0.05f)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(titulo, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Row(verticalAlignment = Alignment.CenterVertically) { Text(valorActual, color = NeonGreen, fontSize = 14.sp); Icon(Icons.Default.ArrowDropDown, null, tint = NeonGreen) }
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }, modifier = Modifier.background(White)) { opciones.forEachIndexed { index, opcion -> DropdownMenuItem(text = { Text(opcion, color = TechBackground) }, onClick = { onSeleccion(index); expandido = false }) } }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ItemAjusteSimple(titulo: String, valor: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp).clip(CardShape).background(Color.White.copy(alpha = 0.05f)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(titulo, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(valor, color = NeonGreen, fontSize = 14.sp) }; Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ItemSwitch(titulo: String, estado: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(titulo, color = White, fontSize = 16.sp, modifier = Modifier.weight(1f)); Switch(checked = estado, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = TechBackground, checkedTrackColor = NeonGreen, uncheckedThumbColor = GrayText, uncheckedTrackColor = White)) }
}

@Composable
fun SectionRedesSociales(viewModel: MainViewModel, navController: NavController) {
    Spacer(modifier = Modifier.height(24.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = { viewModel.login("Google"); navController.navigate("home") { popUpTo("home") { inclusive = true } } }, colors = ButtonDefaults.buttonColors(containerColor = White), shape = ButtonShape, modifier = Modifier.weight(1f).height(50.dp)) { Text("Google", color = TechBackground) }
        Button(onClick = { viewModel.login("Outlook"); navController.navigate("home") { popUpTo("home") { inclusive = true } } }, colors = ButtonDefaults.buttonColors(containerColor = White), shape = ButtonShape, modifier = Modifier.weight(1f).height(50.dp)) { Text("Outlook", color = TechBackground) }
    }
}

// ==========================================
// 5. NAVEGACIÓN Y PANTALLAS
// ==========================================

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Obtenemos textos traducidos
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = TechBackground, drawerContentColor = White) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val user by viewModel.usuarioLogueado.collectAsState()
                    Icon(Icons.Default.AccountCircle, null, tint = NeonGreen, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(user ?: "Invitado", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                }
                Divider(color = GrayText)
                NavigationDrawerItem(label = { Text(t("perfil")) }, selected = false, onClick = { scope.launch { drawerState.close() }; navController.navigate("perfil") }, icon = { Icon(Icons.Default.Person, null, tint = NeonGreen) }, colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = White))
                NavigationDrawerItem(label = { Text(t("ajustes")) }, selected = false, onClick = { scope.launch { drawerState.close() }; navController.navigate("ajustes") }, icon = { Icon(Icons.Default.Settings, null, tint = NeonGreen) }, colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = White))
                NavigationDrawerItem(label = { Text(t("guia")) }, selected = false, onClick = { scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Storefront, null, tint = NeonGreen) }, colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = White))
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { PantallaPrincipal(navController, viewModel, onOpenDrawer = { scope.launch { drawerState.open() } }) }
            composable("login") { PantallaLogin(navController, viewModel, dbHelper) }
            composable("registro") { PantallaRegistro(navController, viewModel, dbHelper) }
            composable("favoritos") { PantallaFavoritos(navController, viewModel) }
            composable("comparar") { PantallaComparar(navController, viewModel) }
            composable("perfil") { PantallaPerfil(navController, viewModel) }
            composable("ajustes") { PantallaAjustes(navController, viewModel) }
            composable("detalle/{productoId}") { backStackEntry -> val id = backStackEntry.arguments?.getString("productoId")?.toIntOrNull(); PantallaDetalle(navController, id, viewModel) }
        }
    }
}

// --- PANTALLAS ---
@Composable
fun PantallaPrincipal(navController: NavController, viewModel: MainViewModel, onOpenDrawer: () -> Unit) {
    val usuario by viewModel.usuarioLogueado.collectAsState()
    val productos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val catSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val context = LocalContext.current

    // Traducciones
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Scaffold(bottomBar = { BottomNavigationBar(navController, "home", viewModel) }, containerColor = TechBackground) { padding ->
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(padding)) {
            item(span = { GridItemSpan(2) }) { HeaderSeccion(usuario, navController, viewModel, onOpenDrawer) }
            item(span = { GridItemSpan(2) }) { BuscadorFuncional(textoBusqueda, t("buscar")) { viewModel.onBusquedaChange(it) } }
            item(span = { GridItemSpan(2) }) { CategoriasFuncionales(catSeleccionada) { viewModel.onCategoriaChange(it) } }
            item(span = { GridItemSpan(2) }) { Text(t("tendencias"), color = White, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.padding(vertical = 12.dp)) }
            if (productos.isEmpty()) { item(span = { GridItemSpan(2) }) { Text(t("sin_resultados"), color = GrayText, textAlign = TextAlign.Center, modifier = Modifier.padding(40.dp)) } }
            else { items(productos) { producto ->
                val precio = viewModel.convertirPrecio(producto.obtenerMejorPrecio())
                CardProductoVisual(producto = producto, precioFormateado = precio, onClick = { navController.navigate("detalle/${producto.id}") }, onFavoritoClick = { viewModel.toggleFavorito(producto.id, context) })
            } }
        }
    }
}

@Composable
fun PantallaPerfil(navController: NavController, viewModel: MainViewModel) {
    val usuario by viewModel.usuarioLogueado.collectAsState()
    val context = LocalContext.current
    // Traducciones
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).background(White).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AccountCircle, null, tint = TechBackground, modifier = Modifier.size(100.dp)); Spacer(modifier = Modifier.height(16.dp)); Text(usuario ?: "Usuario", color = TechBackground, fontSize = 28.sp, fontWeight = FontWeight.Black) }
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) { Icon(Icons.Default.ArrowBack, null, tint = TechBackground) }
        }
        Column(modifier = Modifier.padding(24.dp)) {
            Text(t("datos_cuenta"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(modifier = Modifier.height(16.dp))
            ItemPerfil("Editar Nombre", Icons.Default.Edit) {}; ItemPerfil("Cambiar Contraseña", Icons.Default.Lock) {}; ItemPerfil("Cambiar Correo", Icons.Default.Email) {}
            Spacer(modifier = Modifier.height(32.dp))
            Text(t("vincular_servicios"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(modifier = Modifier.height(16.dp))
            BotonVinculacion(t("vincular_amazon"), Color.White, Color.Black) { Toast.makeText(context, "Vinculando Amazon...", Toast.LENGTH_SHORT).show() }
            Spacer(modifier = Modifier.height(12.dp)); BotonVinculacion(t("vincular_ebay"), Color.White, Color(0xFF0064D2)) { Toast.makeText(context, "Vinculando eBay...", Toast.LENGTH_SHORT).show() }
            Spacer(modifier = Modifier.height(12.dp)); BotonVinculacion(t("vincular_meli"), Color(0xFFFFE600), Color(0xFF2D3277)) { Toast.makeText(context, "Vinculando MeLi...", Toast.LENGTH_SHORT).show() }
        }
    }
}

@Composable
fun PantallaAjustes(navController: NavController, viewModel: MainViewModel) {
    val notifPrecio by viewModel.notifPrecio.collectAsState(); val notifOfertas by viewModel.notifOfertas.collectAsState(); val notifStock by viewModel.notifStock.collectAsState()
    val monedaActual by viewModel.monedaSeleccionada.collectAsState(); val idiomaActual by viewModel.idiomaSeleccionado.collectAsState()
    val context = LocalContext.current

    // Traducciones
    fun t(key: String) = Diccionario.textos[idiomaActual]?.get(key) ?: key

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = White) }; Text(t("ajustes"), color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(30.dp))
        Text(t("general"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(modifier = Modifier.height(16.dp))

        ItemAjusteSelector(t("idioma"), idiomaActual.nombre, Idioma.values().map { it.nombre }) { idx -> viewModel.cambiarIdioma(Idioma.values()[idx]) }
        ItemAjusteSelector(t("divisas"), monedaActual.codigo, Moneda.values().map { it.codigo }) { idx -> viewModel.cambiarMoneda(Moneda.values()[idx]) }

        Spacer(modifier = Modifier.height(32.dp))
        Text(t("notificaciones"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(modifier = Modifier.height(16.dp))
        ItemSwitch(t("bajada_precio"), notifPrecio) { viewModel.toggleNotifPrecio() }; ItemSwitch(t("temp_ofertas"), notifOfertas) { viewModel.toggleNotifOfertas() }; ItemSwitch(t("stock"), notifStock) { viewModel.toggleNotifStock() }
        Spacer(modifier = Modifier.height(32.dp)); Text(t("legal"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(modifier = Modifier.height(16.dp));
        Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 16.dp).clip(CardShape).background(Color.White.copy(alpha = 0.05f)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(t("terminos"), color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(">", color = NeonGreen, fontSize = 14.sp) }
    }
}

@Composable
fun PantallaDetalle(navController: NavController, productoId: Int?, viewModel: MainViewModel) {
    val productosVivos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    val producto = productosVivos.find { it.id == productoId } ?: catalogoCompleto.find { it.id == productoId } ?: catalogoCompleto[0]
    val context = LocalContext.current
    LaunchedEffect(producto.id) { val yaTieneEnVivo = producto.opciones.any { it.nombreTienda.contains("En Vivo") }; if (!yaTieneEnVivo) viewModel.actualizarPreciosReales(producto.nombre) }

    // Traducciones
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    val precioStr = viewModel.convertirPrecio(producto.obtenerMejorPrecio())

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(White)) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { ImagenProductoGrande(producto) }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) { IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(Color.Black.copy(alpha = 0.1f), CircleShape)) { Icon(Icons.Default.ArrowBack, null, tint = Color.Black) }; IconButton(onClick = { viewModel.toggleFavorito(producto.id, context) }, modifier = Modifier.background(White, CircleShape).shadow(4.dp, CircleShape)) { Icon(imageVector = if (producto.esFavorito) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null, tint = if (producto.esFavorito) NeonGreen else GrayText) } }
        }
        Column(modifier = Modifier.padding(24.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text(producto.nombre, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Box(modifier = Modifier.background(NeonGreen, ButtonShape).padding(horizontal = 12.dp, vertical = 6.dp)) { Text("${producto.opciones.firstOrNull()?.rating ?: 4.5}", fontWeight = FontWeight.Bold, color = Color.Black) } }
            Spacer(modifier = Modifier.height(12.dp)); Text(producto.specs, color = GrayText, fontSize = 16.sp); Spacer(modifier = Modifier.height(32.dp)); Text(t("donde_comprar"), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp); Spacer(modifier = Modifier.height(16.dp))
            val opciones = producto.opciones.sortedBy { it.precio }

            // LISTA DE TIENDAS CON PRECIO CONVERTIDO
            opciones.forEach {
                val precioConvertido = viewModel.convertirPrecio(it.precio)
                FilaTiendaClickeable(it, precioConvertido, it.precio == producto.obtenerMejorPrecio())
            }
        }
    }
}

@Composable
fun PantallaComparar(navController: NavController, viewModel: MainViewModel) {
    var p1 by remember { mutableStateOf<ProductoUi?>(null) }; var p2 by remember { mutableStateOf<ProductoUi?>(null) }; var showSel by remember { mutableStateOf(false) }; var slot by remember { mutableStateOf(0) }
    val list = remember(slot, p1) { if (slot == 2 && p1 != null) catalogoCompleto.filter { it.categoria == p1!!.categoria && it.id != p1!!.id } else catalogoCompleto }

    // Traducciones
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Scaffold(bottomBar = { BottomNavigationBar(navController, "comparar", viewModel) }, containerColor = TechBackground) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(t("comparador"), color = White, fontSize = 24.sp, fontWeight = FontWeight.Black); Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Box(modifier = Modifier.clickable { slot = 1; showSel = true }) { SelectorProductoDinamico(p1, t("elige")) }; Text(t("vs"), color = NeonGreen, fontSize = 30.sp, fontWeight = FontWeight.Black); Box(modifier = Modifier.clickable { slot = 2; showSel = true }) { SelectorProductoDinamico(p2, t("elige")) } }
            Spacer(modifier = Modifier.height(40.dp))
            if (p1 != null && p2 != null) { Card(colors = CardDefaults.cardColors(containerColor = CardBackground), shape = CardShape) { Column(modifier = Modifier.padding(20.dp)) { RowTabla("Producto", p1!!.nombre, p2!!.nombre); Divider(); val pr1 = viewModel.convertirPrecio(p1!!.obtenerMejorPrecio()); val pr2 = viewModel.convertirPrecio(p2!!.obtenerMejorPrecio())
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(pr1, color = if(p1!!.obtenerMejorPrecio() <= p2!!.obtenerMejorPrecio()) Color(0xFF00AA00) else TechBackground, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text(t("precio"), color = GrayText, fontSize = 12.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.Center); Text(pr2, color = if(p2!!.obtenerMejorPrecio() <= p1!!.obtenerMejorPrecio()) Color(0xFF00AA00) else TechBackground, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End) }; Divider()
                // Calificación
                val r1 = p1!!.opciones.map { it.rating }.average(); val r2 = p2!!.opciones.map { it.rating }.average()
                RowTabla(t("calificacion"), String.format("%.1f ★", r1), String.format("%.1f ★", r2)); Divider()
                // Tiendas
                val t1 = p1!!.opciones.joinToString("\n") { it.nombreTienda }; val t2 = p2!!.opciones.joinToString("\n") { it.nombreTienda }
                RowTabla(t("disponible"), t1, t2); Divider()

                RowTabla(t("specs"), p1!!.specs, p2!!.specs)
            } } }
        }
        if (showSel) { Dialog(onDismissRequest = { showSel = false }) { Card(modifier = Modifier.fillMaxWidth().height(500.dp), colors = CardDefaults.cardColors(containerColor = White)) { Column(modifier = Modifier.padding(16.dp)) { Text(t("sugeridos"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TechBackground); Spacer(modifier = Modifier.height(8.dp)); LazyColumn { items(list) { prod -> Row(modifier = Modifier.fillMaxWidth().clickable { if (slot == 1) { p1 = prod; p2 = null } else p2 = prod; showSel = false }.padding(16.dp)) { Text(prod.nombre, color = Color.Black) } } } } } } }
    }
}

@Composable
fun PantallaFavoritos(navController: NavController, viewModel: MainViewModel) {
    val productosVivos by viewModel.productosVisibles.collectAsState(initial = emptyList())
    val favoritos = productosVivos.filter { it.esFavorito }
    val context = LocalContext.current

    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Scaffold(bottomBar = { BottomNavigationBar(navController, "favoritos", viewModel) }, containerColor = TechBackground) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(t("favoritos"), color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(favoritos) { prod ->
                    val precio = viewModel.convertirPrecio(prod.obtenerMejorPrecio())
                    CardProductoVisual(prod, precio, { navController.navigate("detalle/${prod.id}") }, { viewModel.toggleFavorito(prod.id, context) })
                }
            }
        }
    }
}

@Composable
fun PantallaLogin(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuarioText by remember { mutableStateOf("") }; var passText by remember { mutableStateOf("") }; val context = LocalContext.current
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).padding(32.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp)); Image(painter = painterResource(id = R.drawable.logosinfondo), contentDescription = "Logo", modifier = Modifier.size(180.dp))
        Spacer(modifier = Modifier.height(40.dp)); Text(t("bienvenido"), color = White, fontWeight = FontWeight.Bold, fontSize = 28.sp); Text(t("continuar"), color = GrayText, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(40.dp)); CampoTextoBlanco(usuarioText, { usuarioText = it }, t("usuario_correo")); Spacer(modifier = Modifier.height(16.dp)); CampoTextoBlanco(passText, { passText = it }, t("pass"), isPassword = true)
        Spacer(modifier = Modifier.height(32.dp)); Button(onClick = { try { val nombreReal = dbHelper.validarLogin(usuarioText, passText); if(nombreReal != null) { viewModel.login(nombreReal); navController.popBackStack() } else { Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show() } } catch (e: Exception) { Toast.makeText(context, "Error DB", Toast.LENGTH_LONG).show() } }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), shape = ButtonShape, modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, ButtonShape)) { Text(t("inicia_sesion"), color = TechBackground, fontWeight = FontWeight.Black, fontSize = 18.sp) }
        SectionRedesSociales(viewModel, navController)
        Spacer(modifier = Modifier.height(24.dp)); Row { Text(t("no_cuenta"), color = GrayText); Text(t("registrate"), color = NeonGreen, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("registro") }) }
    }
}

@Composable
fun PantallaRegistro(navController: NavController, viewModel: MainViewModel, dbHelper: DatabaseHelper) {
    var usuario by remember { mutableStateOf("") }; var correo by remember { mutableStateOf("") }; var pass by remember { mutableStateOf("") }; var passConfirm by remember { mutableStateOf("") }; val context = LocalContext.current
    val idioma by viewModel.idiomaSeleccionado.collectAsState()
    fun t(key: String) = Diccionario.textos[idioma]?.get(key) ?: key

    Column(modifier = Modifier.fillMaxSize().background(TechBackground).padding(32.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp)); Text(t("crear_cuenta"), color = White, fontWeight = FontWeight.Bold, fontSize = 28.sp); Text(t("unete"), color = GrayText, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(40.dp)); CampoTextoBlanco(usuario, { usuario = it }, t("usuario")); Spacer(modifier = Modifier.height(16.dp)); CampoTextoBlanco(correo, { correo = it }, t("correo"), keyboardType = KeyboardType.Email); Spacer(modifier = Modifier.height(16.dp)); CampoTextoBlanco(pass, { pass = it }, t("pass"), isPassword = true); Spacer(modifier = Modifier.height(16.dp)); CampoTextoBlanco(passConfirm, { passConfirm = it }, t("pass_conf"), isPassword = true)
        Spacer(modifier = Modifier.height(32.dp)); Button(onClick = { if (usuario.isEmpty() || correo.isEmpty() || pass.isEmpty()) { Toast.makeText(context, "Llena todo", Toast.LENGTH_SHORT).show() } else if (!esPasswordSegura(pass)) { Toast.makeText(context, "Insegura", Toast.LENGTH_LONG).show() } else if (pass != passConfirm) { Toast.makeText(context, "No coinciden", Toast.LENGTH_SHORT).show() } else { if (dbHelper.existeUsuarioOCorreo(usuario, correo)) { Toast.makeText(context, "Ya existe", Toast.LENGTH_LONG).show() } else { if (dbHelper.registrarUsuario(usuario, correo, pass)) { viewModel.login(usuario); navController.navigate("home") { popUpTo("home") { inclusive = true } } } } } }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), shape = ButtonShape, modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, ButtonShape)) { Text(t("registrate"), color = TechBackground, fontWeight = FontWeight.Black, fontSize = 18.sp) }
        Spacer(modifier = Modifier.height(24.dp)); Row { Text(t("ya_cuenta"), color = GrayText); Text(t("inicia_sesion"), color = NeonGreen, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("login") }) }
    }
}