package com.example.u_study.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.u_study.data.database.entities.Library

@Composable
fun ListLibraryItem(
    library: Library,
    onClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    isAuthenticated: Boolean = true
) {

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Icon(
                imageVector = if (library.isVisited) Icons.Filled.CheckCircle else Icons.AutoMirrored.Outlined.MenuBook,
                contentDescription = if (library.isVisited) "Visitata" else "Library Icon",
                tint = if (library.isVisited) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = library.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = library.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(8.dp))

            if (isAuthenticated) {
                IconButton(onClick = onFavouriteClick) {
                    Icon(
                        imageVector = if (library.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favourite",
                        tint = if (library.isFavourite) Color.Red else LocalContentColor.current
                    )
                }
            }

        }
    }
}