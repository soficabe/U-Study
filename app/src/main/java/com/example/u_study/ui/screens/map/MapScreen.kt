package com.example.u_study.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import com.example.u_study.utils.*
import com.example.u_study.data.database.entities.Library
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.composables.AppBar
import kotlinx.coroutines.launch

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun MapScreen(
    state: MapState,
    libraryIdToZoom: Int? = null,
    viewModel: MapViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val coordinates by locationService.coordinates.collectAsState()
    val libraries by viewModel.libraries.collectAsState()
    val visited by viewModel.visitedLibraries.collectAsState()

    var showLocationDisabledWarning by remember { mutableStateOf(false) }
    var showPermissionDeniedWarning by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedWarning by remember { mutableStateOf(false) }
    var requestLocation by remember { mutableStateOf(false) }

    var mapViewInstance: MapView? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    // Stato salvabile per posizione e zoom della mappa
    var mapCenter by rememberSaveable {
        mutableStateOf(
            if (libraryIdToZoom != null) {
                val lib = libraries.find { it.id == libraryIdToZoom }
                if (lib != null) "lat:${lib.latitude},lng:${lib.longitude}" else "lat:44.5,lng:11.3"
            } else "lat:44.5,lng:11.3"
        )
    }

    var mapZoom by rememberSaveable {
        mutableDoubleStateOf(if (libraryIdToZoom != null) 17.0 else 11.0)
    }

    // Stato salvabile per la posizione dell'utente
    var savedUserLocation by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    // Funzione per parsare il centro della mappa dal string salvato
    fun parseMapCenter(centerString: String): GeoPoint {
        return try {
            val parts = centerString.split(",")
            val lat = parts[0].substringAfter("lat:").toDouble()
            val lng = parts[1].substringAfter("lng:").toDouble()
            GeoPoint(lat, lng)
        } catch (e: Exception) {
            GeoPoint(44.5, 11.3) // fallback
        }
    }

    // Funzione per parsare la posizione dell'utente dal string salvato
    fun parseUserLocation(locationString: String?): GeoPoint? {
        return try {
            locationString?.let {
                val parts = it.split(",")
                val lat = parts[0].substringAfter("lat:").toDouble()
                val lng = parts[1].substringAfter("lng:").toDouble()
                GeoPoint(lat, lng)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Funzione per aggiornare lo stato della mappa
    fun updateMapState(mapView: MapView) {
        val center = mapView.mapCenter
        mapCenter = "lat:${center.latitude},lng:${center.longitude}"
        mapZoom = mapView.zoomLevelDouble
    }

    val locationPermission = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        when {
            statuses.any { it.value.isGranted } -> {
                requestLocation = true
            }
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                showPermissionPermanentlyDeniedWarning = true
            else -> showPermissionDeniedWarning = true
        }
    }

    // Richiedi subito la posizione appena entri nella mappa!
    LaunchedEffect(Unit) {
        locationService.getCurrentLocation()
    }

    if (requestLocation) {
        LaunchedEffect(Unit) {
            locationService.getCurrentLocation()
            requestLocation = false
        }
    }

    // Aggiorna lo stato salvato quando cambiano le coordinate
    LaunchedEffect(coordinates) {
        coordinates?.let { coords ->
            savedUserLocation = "lat:${coords.latitude},lng:${coords.longitude}"
        }
    }

    // Aggiorna visitati (side effect) appena hai la posizione O la lista delle biblioteche
    LaunchedEffect(coordinates, libraries) {
        Log.d("MapScreen", "LaunchedEffect triggered, coordinates=$coordinates")
        coordinates?.let { coord ->
            libraries.forEach { library ->
                val near = isNear(coord, library)
                Log.d("MapScreen", "isNear(${library.name}) = $near")
                if (!visited.contains(library.id) && near) {
                    Log.d("MapScreen", "Mark as visited: ${library.name} (id=${library.id})")
                    viewModel.markLibraryVisited(library.id)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(R.string.mapScreen_name), navController, isAuthenticated = state.isAuthenticated)
        },
        bottomBar = {
            com.example.u_study.ui.composables.NavigationBar(navController = navController, isAutheticated = state.isAuthenticated)
        }
    ){ contentPadding ->
        Column(Modifier
            .padding(contentPadding)
            .fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        val controller = controller

                        // Usa lo stato salvato invece dei valori di default
                        val startPoint = parseMapCenter(mapCenter)
                        controller.setZoom(mapZoom)
                        controller.setCenter(startPoint)

                        overlays.clear()
                        libraries.forEach { library ->
                            val marker = Marker(this)
                            marker.position = GeoPoint(library.latitude.toDouble(), library.longitude.toDouble())
                            marker.title = library.name
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.icon = context.getDrawable(
                                if (visited.contains(library.id))
                                    R.drawable.ic_marker_visited
                                else
                                    R.drawable.ic_marker_default
                            )
                            overlays.add(marker)
                        }
                        mapViewInstance = this
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    libraries.forEach { library ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(library.latitude.toDouble(), library.longitude.toDouble())
                        marker.title = library.name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.icon = context.getDrawable(
                            if (visited.contains(library.id))
                                com.example.u_study.R.drawable.ic_marker_visited
                            else
                                com.example.u_study.R.drawable.ic_marker_default
                        )
                        mapView.overlays.add(marker)
                    }

                    // Aggiungi il marker per la posizione dell'utente
                    // Usa prima le coordinate attuali, poi quelle salvate come fallback
                    val userLocationToShow = coordinates ?: parseUserLocation(savedUserLocation)?.let { geoPoint ->
                        Coordinates(geoPoint.latitude, geoPoint.longitude)
                    }

                    userLocationToShow?.let { coords ->
                        val myLocationMarker = Marker(mapView).apply {
                            icon = context.getDrawable(R.drawable.ic_my_location_marker)
                            position = GeoPoint(coords.latitude, coords.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = "La mia posizione"
                        }
                        mapView.overlays.add(myLocationMarker)
                    }

                    if (libraryIdToZoom != null) {
                        val lib = libraries.find { it.id == libraryIdToZoom }
                        if (lib != null) {
                            mapView.controller.setCenter(GeoPoint(lib.latitude.toDouble(), lib.longitude.toDouble()))
                            mapView.controller.setZoom(17.0)
                            // Aggiorna anche lo stato salvato
                            mapCenter = "lat:${lib.latitude},lng:${lib.longitude}"
                            mapZoom = 17.0
                        }
                    }

                    // Forza la mappa a ridisegnarsi per mostrare i nuovi marker
                    mapView.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    if (locationPermission.statuses.any { it.value.isGranted }) {
                        scope.launch {
                            // 1. Chiedi al service di calcolare la posizione attuale
                            val currentLocation = locationService.getCurrentLocation()

                            // 2. Se la posizione è stata trovata...
                            currentLocation?.let { coords ->
                                // 3. Centra la mappa e aggiorna lo stato salvato
                                mapViewInstance?.let { mapView ->
                                    mapView.controller.animateTo(
                                        GeoPoint(coords.latitude, coords.longitude),
                                        17.0,
                                        1000L
                                    )
                                    // Aggiorna lo stato salvato con la nuova posizione
                                    mapCenter = "lat:${coords.latitude},lng:${coords.longitude}"
                                    mapZoom = 17.0
                                }
                            }
                        }
                    } else {
                        locationPermission.launchPermissionRequest()
                    }
                }) {
                    Text(stringResource(R.string.update_position_button))
                }
            }

            if (showPermissionDeniedWarning) {
                AlertDialog(
                    title = { Text("Permesso posizione negato") },
                    text = { Text("Il permesso è necessario per segnare le biblioteche visitate.") },
                    confirmButton = {
                        TextButton(onClick = {
                            locationPermission.launchPermissionRequest()
                            showPermissionDeniedWarning = false
                        }) { Text("Abilita") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionDeniedWarning = false }) { Text("Annulla") }
                    },
                    onDismissRequest = { showPermissionDeniedWarning = false }
                )
            }

            if (showLocationDisabledWarning) {
                AlertDialog(
                    title = { Text("GPS disabilitato") },
                    text = { Text("Abilita il GPS per continuare.") },
                    confirmButton = {
                        TextButton(onClick = {
                            locationService.openLocationSettings()
                            showLocationDisabledWarning = false
                        }) { Text("Abilita") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLocationDisabledWarning = false }) { Text("Annulla") }
                    },
                    onDismissRequest = { showLocationDisabledWarning = false }
                )
            }

            if (showPermissionPermanentlyDeniedWarning) {
                AlertDialog(
                    title = { Text("Permesso posizione permanentemente negato") },
                    text = { Text("Vai nelle impostazioni dell'app per abilitare il permesso posizione.") },
                    confirmButton = {
                        TextButton(onClick = {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = android.net.Uri.fromParts("package", context.packageName, null)
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                            showPermissionPermanentlyDeniedWarning = false
                        }) { Text("Impostazioni") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionPermanentlyDeniedWarning = false }) { Text("Annulla") }
                    },
                    onDismissRequest = { showPermissionPermanentlyDeniedWarning = false }
                )
            }
        }
    }

    // Aggiungi un DisposableEffect per salvare lo stato quando il composable viene distrutto
    DisposableEffect(mapViewInstance) {
        onDispose {
            mapViewInstance?.let { mapView ->
                updateMapState(mapView)
            }
        }
    }
}

fun isNear(coords: Coordinates, library: Library, radiusMeters: Double = 5000.0): Boolean {
    return distanceBetween(coords, Coordinates(library.latitude.toDouble(), library.longitude.toDouble())) < radiusMeters
}