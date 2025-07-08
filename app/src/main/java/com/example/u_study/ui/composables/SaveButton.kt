package com.example.u_study.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.u_study.ui.theme.Orange

//in parole molto povere è il bottone arancione

@Composable
fun SaveButton(text: String) {
    Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Orange
        )
    ) {
        Text(text)
    }
}