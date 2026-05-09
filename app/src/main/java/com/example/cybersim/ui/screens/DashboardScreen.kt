package com.example.cybersim.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cybersim.ui.theme.AccentCyan
import com.example.cybersim.ui.theme.AccentIndigo
import com.example.cybersim.ui.theme.AppCard
import com.example.cybersim.ui.theme.ElectricBlue
import com.example.cybersim.ui.theme.GlassStroke
import com.example.cybersim.ui.theme.NeonCyan
import com.example.cybersim.ui.theme.NeonGreen
import com.example.cybersim.ui.theme.NeonPurple
import com.example.cybersim.ui.theme.SemanticDanger
import com.example.cybersim.ui.theme.SemanticSuccess
import com.example.cybersim.ui.theme.SemanticWarning
import com.example.cybersim.ui.theme.SoftMagenta
import com.example.cybersim.ui.theme.TextPrimary
import com.example.cybersim.ui.theme.TextSecondary
import com.example.cybersim.ui.theme.TextTertiary
import com.example.cybersim.ui.theme.appTopBarTint
import com.example.cybersim.ui.theme.glassCard
import com.example.cybersim.ui.theme.neonCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTerminal: () -> Unit,
    onNavigateToPhishing: () -> Unit,
    onNavigateToCracker: () -> Unit,
    onNavigateToWifiAttack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val activeConnections by viewModel.activeConnections.collectAsState()
    val packetsSniffed by viewModel.packetsSniffed.collectAsState()
    val threatLevel by viewModel.threatLevel.collectAsState()
    val liveLogs by viewModel.liveLogs.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.appTopBarTint(),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                "CyberSim",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                        }
                        Text(
                            "Created by Harshal Mahadik",
                            color = TextTertiary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = TextPrimary,
                )
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { ThreatLevelCard(threatLevel) }
            item { LivePacketFlow(threatLevel) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MetricCard(
                        title = "Active connections",
                        value = activeConnections,
                        accentBrush = Brush.linearGradient(listOf(ElectricBlue, AccentCyan)),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Packets analyzed",
                        value = "$packetsSniffed k",
                        accentBrush = Brush.linearGradient(listOf(AccentIndigo, SoftMagenta)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item { LiveLogViewer(logs = liveLogs) }
            item {
                Text(
                    text = "Simulation labs",
                    color = NeonCyan,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            item {
                LabNavCard(
                    title = "Phishing intelligence",
                    subtitle = "URL & email heuristics",
                    onClick = onNavigateToPhishing,
                    brush = Brush.horizontalGradient(listOf(AccentCyan.copy(0.35f), ElectricBlue.copy(0.25f)))
                )
            }
            item {
                LabNavCard(
                    title = "Password resilience",
                    subtitle = "Entropy & dictionary modeling",
                    onClick = onNavigateToCracker,
                    brush = Brush.horizontalGradient(listOf(SoftMagenta.copy(0.3f), AccentIndigo.copy(0.25f)))
                )
            }
            item {
                LabNavCard(
                    title = "Wireless posture",
                    subtitle = "Handshake capture education",
                    onClick = onNavigateToWifiAttack,
                    brush = Brush.horizontalGradient(listOf(SemanticSuccess.copy(0.28f), AccentCyan.copy(0.22f)))
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CommandConsoleButton(onClick = onNavigateToTerminal)
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabNavCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    brush: Brush,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .glassCard(corner = 20.dp, neonColor = NeonCyan),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = NeonCyan, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = NeonCyan.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Text("→", color = NeonCyan, fontSize = 22.sp)
        }
    }
}

@Composable
fun ThreatLevelCard(threatLevel: String) {
    val baseColor = when (threatLevel) {
        "LOW" -> SemanticSuccess
        "MEDIUM" -> SemanticWarning
        "HIGH", "CRITICAL" -> SemanticDanger
        "DISCONNECTED" -> TextTertiary
        else -> SemanticDanger
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(corner = 24.dp, elevated = true, neonColor = baseColor)
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(baseColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = baseColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Threat posture",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = threatLevel,
                    color = baseColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun LiveLogViewer(logs: List<SecurityLog>) {
    val listState = rememberLazyListState()

    androidx.compose.runtime.LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(corner = 20.dp, neonColor = NeonCyan)
            .padding(16.dp)
    ) {
        Text(
            "Live security log",
            style = MaterialTheme.typography.titleMedium,
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(color = NeonCyan)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs.size) { index ->
                val log = logs[index]
                val accent = when (log.level) {
                    "INFO" -> AccentCyan
                    "WARNING", "ALERT" -> SemanticWarning
                    "CRITICAL" -> SemanticDanger
                    else -> TextPrimary
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = log.timestamp,
                        color = TextTertiary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(56.dp)
                    )
                    Text(
                        text = log.level,
                        color = accent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(76.dp)
                    )
                    Text(
                        text = log.message,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, accentBrush: Brush, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .glassCard(corner = 20.dp, elevated = true, neonColor = NeonCyan)
            .padding(20.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = NeonCyan.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                brush = accentBrush,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun CommandConsoleButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .neonCard(corner = 18.dp, neonColor = NeonPurple),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(NeonPurple.copy(0.2f), NeonCyan.copy(0.1f), Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Open command console",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = NeonPurple,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}
