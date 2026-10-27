package cl.duoc.costostock.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.costostock.R
import cl.duoc.costostock.viewmodel.CatalogoViewModel
import coil.compose.AsyncImage

@Composable
fun CatalogoScreen(viewModel: CatalogoViewModel, navController: NavController) {
    val lotes by viewModel.lotesVisibles.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    var permisosConcedidos by remember { mutableStateOf(false) }
    var mostrarMapa by remember { mutableStateOf(false) }

    val contexto = androidx.compose.ui.platform.LocalContext.current
    val gestorUbicacion = remember { cl.duoc.costostock.util.GestorUbicacion(contexto) }

    ManejadorPermisosGPS(
        onPermisoConcedido = {
            permisosConcedidos = true
            gestorUbicacion.obtenerUbicacionActual { location ->
                location?.let {
                    viewModel.actualizarUbicacionUsuario(it.latitude, it.longitude)
                }
            }
        },
        onPermisoDenegado = { permisosConcedidos = false }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("publicar_lote") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Publicar nueva oferta",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logocostostockoficial),
                    contentDescription = "Logo Oficial CostoStock",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(32.dp)
                        .weight(1f),
                    alignment = Alignment.CenterStart
                )

                Button(onClick = { mostrarMapa = !mostrarMapa }) {
                    Text(text = if (mostrarMapa) "Ver Lista" else "Ver Mapa")
                }

                IconButton(
                    onClick = {
                        navController.navigate("auth_menu") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar productos o comercios...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (!permisosConcedidos) {
                Text(
                    text = "Se requiere acceso a la ubicación para calcular la distancia.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = mostrarMapa,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "TransicionListaMapa"
            ) { isMap ->
                if (isMap) {
                    MapaOfertasScreen(viewModel, navController)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(lotes) { lote ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("detalle_oferta/${lote.idLote}") }
                            ) {
                                Box {
                                    AsyncImage(
                                        model = lote.urlsFotografiasActuales.firstOrNull(),
                                        contentDescription = "Imagen de ${lote.nombreProducto}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEEEEEE))
                                    )

                                    val esRecienPublicado = (System.currentTimeMillis() - lote.fechaCapturaFotoMillis) < 86_400_000L

                                    if (esRecienPublicado) {
                                        Surface(
                                            color = Color.White.copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Recién publicado",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$${lote.precioOfertaFinal}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$${lote.precioNormalSinDescuento}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }

                                Text(
                                    text = lote.nombreProducto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}