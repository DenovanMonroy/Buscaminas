package com.example.minesweeper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.minesweeper.R

@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onLoadGameClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BUSCAMINAS",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_minesweeper),
            contentDescription = "Minesweeper Logo",
            modifier = Modifier.height(120.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNewGameClick,
            modifier = Modifier.width(200.dp)
        ) {
            Text("Nueva Partida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoadGameClick,
            modifier = Modifier.width(200.dp)
        ) {
            Text("Cargar Partida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onOptionsClick,
            modifier = Modifier.width(200.dp)
        ) {
            Text("Opciones")
        }
    }
}