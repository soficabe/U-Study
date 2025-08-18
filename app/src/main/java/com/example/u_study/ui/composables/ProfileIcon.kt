package com.example.u_study.ui.composables

/*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* Icona profilo usata in ProfileScreen.kt e ModifyUserScreen.kt.
 * Presenta l'immagine profilo arrotondata (da testare con immagine vera)
 * e il bottone + in basso a destra (regolato di posizione tramite offset).
 * L'immagine è fittizia e va cambiata.
 * PROBLEMA: teoricamente l'user lo vediamo circolare solo perché l'icona è
 * circolare. Probabilmente con un'immagine quadrata è tutto sfasato. Va
 * sistemato (ma come?)
*/
@Composable
fun ProfileIcon() {
    Box(contentAlignment = Alignment.BottomEnd) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            //painter = painterResource(id = R.mipmap.u_study_icon_foreground),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .offset(x = (-6).dp, y = (-6).dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .size(30.dp)

        ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Change Picture",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)

        )
    }

    }
}
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

/**
 * Composable per mostrare l'immagine profilo dell'utente.
 * Supporta il caricamento di immagini da URL con fallback su icona predefinita.
 *
 * @param imageUrl URL dell'immagine profilo (può essere null)
 * @param isClickable Se true, mostra un overlay con icona camera
 * @param onClick Callback per il click (solo se isClickable = true)
 * @param modifier Modifier opzionale
 */
@Composable
fun ProfileIcon(
    imageUrl: String? = null,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .then(
                if (isClickable && onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        if (!imageUrl.isNullOrBlank()) {
            // Carica immagine da URL
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Immagine profilo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                },
                error = {
                    // Fallback su icona predefinita in caso di errore
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Immagine profilo predefinita",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        } else {
            // Mostra icona predefinita se non c'è URL
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Immagine profilo predefinita",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Overlay camera se clickable
        if (isClickable) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Cambia immagine profilo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}