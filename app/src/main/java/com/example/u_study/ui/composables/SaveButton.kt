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

/* bottone arancione presente in varie schermate: LoginScreen.kt,
 * RegisterScreen.kt, ProfileScreen.kt, ModifyUserScreen.kt.
 * L'idea è quella di passare in input l'azione che viene eseguita
 * al click, in quanto a seconda della schermata in cui viene premuto,
 * l'azione è molto diversa.
 *
 */
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