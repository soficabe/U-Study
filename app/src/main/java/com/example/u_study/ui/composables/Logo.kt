package com.example.u_study.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.u_study.R


/* Logo usato in RegisterScreen.kt e LoginScreen.kt.
 * è l'unione di immagine + testo blu.
 */
@Composable
fun Logo () {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.offset(x = (-8).dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.u_study_icon_foreground),
            contentDescription = "Logo App",
            modifier = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text="U-STUDY",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF237ECD),
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}