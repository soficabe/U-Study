package com.example.u_study.ui.composables

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
