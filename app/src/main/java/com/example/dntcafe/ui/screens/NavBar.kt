package com.example.dntcafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dntcafe.ui.theme.BluePrimary

@Composable
fun AppDrawer(userName: String, onItemSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Greeting Section
        Text(text = "Hello! $userName", fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        DrawerItem("Food Menu", Icons.Default.Restaurant, selected = true) { onItemSelected("food_menu") }
        Spacer(modifier = Modifier.height(8.dp))

        DrawerSection("Account")
        //DrawerItem("Profile", Icons.Default.Person) { onItemSelected("profile") }
        DrawerItem("My Orders", Icons.AutoMirrored.Filled.List) { onItemSelected("my_orders") }
        DrawerItem("Orders History", Icons.Default.History) { onItemSelected("orders_history") }
        /*
        DrawerSection("Support")
        DrawerItem("Share App", Icons.Default.Share) { onItemSelected("share_app") }
        DrawerItem("Report Bug", Icons.Default.BugReport) { onItemSelected("report_bug") }
        DrawerItem("Contact Us", Icons.Default.Phone) { onItemSelected("contact_us") }

        DrawerItem("Update Menu", Icons.Default.Refresh) { onItemSelected("update_menu") }
        DrawerItem("Settings", Icons.Default.Settings) { onItemSelected("settings") }
        */
        Spacer(modifier = Modifier.weight(1f)) // Push logout to bottom
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
        DrawerItem("Log Out", Icons.AutoMirrored.Filled.ExitToApp) { onItemSelected("logout") }
    }
}

@Composable
fun DrawerItem(title: String, icon: ImageVector, selected: Boolean = false, onClick: () -> Unit) {
    val background = if (selected) Color(0xFFEEF1FF) else Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = BluePrimary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}

@Composable
fun DrawerSection(title: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    Spacer(modifier = Modifier.height(8.dp))
}
