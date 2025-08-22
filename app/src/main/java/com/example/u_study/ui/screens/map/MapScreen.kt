package com.example.u_study.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun MapScreen(
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

    if (requestLocation) {
        LaunchedEffect(Unit) {
            locationService.getCurrentLocation()
            requestLocation = false
        }
    }

    // Aggiorna visitati (side effect)
    LaunchedEffect(coordinates, libraries) {
        coordinates?.let { coord ->
            libraries.forEach { library ->
                if (!visited.contains(library.id) && isNear(coord, library)) {
                    viewModel.markLibraryVisited(library.id)
                }
            }
        }
    }

    Scaffold(
        topBar = {
        AppBar(stringResource(R.string.mapScreen_name), navController)
        },
        bottomBar = {
            com.example.u_study.ui.composables.NavigationBar(navController = navController)
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
                        val startPoint = if (libraryIdToZoom != null) {
                            val lib = libraries.find { it.id == libraryIdToZoom }
                            if (lib != null) GeoPoint(lib.latitude.toDouble(), lib.longitude.toDouble()) else GeoPoint(44.5, 11.3)
                        } else GeoPoint(44.5, 11.3)
                        controller.setZoom(if (libraryIdToZoom != null) 17.0 else 11.0)
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
                    if (libraryIdToZoom != null) {
                        val lib = libraries.find { it.id == libraryIdToZoom }
                        if (lib != null) {
                            mapView.controller.setCenter(GeoPoint(lib.latitude.toDouble(), lib.longitude.toDouble()))
                            mapView.controller.setZoom(17.0)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    if (locationPermission.statuses.any { it.value.isGranted }) {
                        requestLocation = true
                    } else {
                        locationPermission.launchPermissionRequest()
                    }
                }) {
                    Text("Aggiorna posizione")
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

}

fun isNear(coords: Coordinates, library: Library, radiusMeters: Double = 50.0): Boolean {
    return distanceBetween(coords, Coordinates(library.latitude.toDouble(), library.longitude.toDouble())) < radiusMeters
}