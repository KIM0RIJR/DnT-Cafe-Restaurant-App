package com.example.dntcafe.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dntcafe.ui.theme.BluePrimary

@Composable
fun AccountRegisteredDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Sent",
                    tint = BluePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("ACCOUNT ACTIVATION", color = BluePrimary)
            }
        },
        text = {
            Text(
                "You need to activate your account to complete registration.\n\nAn E-mail has been sent to your device. Activate it to access your account.",
                color = Color.DarkGray
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text("OK", color = Color.White)
            }
        }
    )
}
