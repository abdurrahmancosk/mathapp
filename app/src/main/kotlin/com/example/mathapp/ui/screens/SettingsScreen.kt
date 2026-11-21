package com.example.mathapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathapp.ui.theme.LightBlue

@Composable
fun SettingsScreen() {
    val (darkMode, setDarkMode) = remember { mutableStateOf(false) }
    val (soundEnabled, setSoundEnabled) = remember { mutableStateOf(true) }
    val (notificationsEnabled, setNotificationsEnabled) = remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚙️ Ayarlar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Text(
                text = "Görüntü",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingItem(
                        icon = Icons.Default.Brightness4,
                        title = "Koyu Mod",
                        description = "Gece modu aç/kapat",
                        isToggle = true,
                        isEnabled = darkMode,
                        onToggle = { setDarkMode(it) }
                    )
                }
            }
        }

        item {
            Text(
                text = "Ses & Bildirimler",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingItem(
                        icon = Icons.Default.VolumeUp,
                        title = "Ses Efektleri",
                        description = "Cevap seslerini aç/kapat",
                        isToggle = true,
                        isEnabled = soundEnabled,
                        onToggle = { setSoundEnabled(it) }
                    )
                    Divider()
                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Bildirimler",
                        description = "Günlük hatırlatmaları aç/kapat",
                        isToggle = true,
                        isEnabled = notificationsEnabled,
                        onToggle = { setNotificationsEnabled(it) }
                    )
                }
            }
        }

        item {
            Text(
                text = "Veriler",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingItem(
                        icon = Icons.Default.Download,
                        title = "Verileri Dışa Aktar",
                        description = "İlerlemenizi yedekleyin",
                        onClick = {}
                    )
                    Divider()
                    SettingItem(
                        icon = Icons.Default.Delete,
                        title = "Tüm Verileri Sil",
                        description = "Tüm ilerlemeyi sıfırla",
                        onClick = {}
                    )
                }
            )
        }

        item {
            Text(
                text = "Hakkında",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📚 Matematik Öğren",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "v1.0.0",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Bloom'un Taksonomisi ile 2. Sınıf Matematik",
                        fontSize = 11.sp,
                        color = androidx.compose.ui.graphics.Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.material.icons.Icons.Default,
    title: String,
    description: String = "",
    isToggle: Boolean = false,
    isEnabled: Boolean = false,
    onToggle: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        }

        if (isToggle && onToggle != null) {
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle(it) }
            )
        }
    }
}
