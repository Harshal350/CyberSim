package com.example.cybersim.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import com.example.cybersim.ui.theme.NeonBlue
import com.example.cybersim.ui.theme.NeonCyan
import com.example.cybersim.ui.theme.NeonGreen
import com.example.cybersim.ui.theme.NeonPink
import com.example.cybersim.ui.theme.NeonPurple
import com.example.cybersim.ui.theme.SemanticDanger
import com.example.cybersim.ui.theme.SemanticSuccess
import com.example.cybersim.ui.theme.SemanticWarning
import com.example.cybersim.ui.theme.TextPrimary
import com.example.cybersim.ui.theme.TextSecondary
import com.example.cybersim.ui.theme.TextTertiary
import com.example.cybersim.ui.theme.appTopBarTint
import com.example.cybersim.ui.theme.glassCard
import com.example.cybersim.ui.theme.neonGlow
import com.example.cybersim.ui.theme.GlassStroke
import kotlinx.coroutines.delay

data class WifiNetwork(val ssid: String, val bssid: String, val security: String, val signal: Int)

private enum class HandshakeVisualPhase {
    Prepare,
    Deauth,
    Disconnected,
    Handshake,
    Captured,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiAttackScreen(onNavigateBack: () -> Unit) {
    var isScanning by remember { mutableStateOf(false) }
    var networks by remember { mutableStateOf(emptyList<WifiNetwork>()) }
    var selectedNetwork by remember { mutableStateOf<WifiNetwork?>(null) }
    var attackStatus by remember { mutableStateOf("") }
    var isAttacking by remember { mutableStateOf(false) }
    var handshakeVisualPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            networks = emptyList()
            selectedNetwork = null
            attackStatus = ""
            delay(3000) // Longer scan for radar animation
            networks = listOf(
                WifiNetwork("Corporate_LAN_5G", "00:1A:2B:3C:4D:5E", "WPA3", -45),
                WifiNetwork("Guest_WiFi", "11:22:33:44:55:66", "WPA2", -60),
                WifiNetwork("Linksys_Router", "AA:BB:CC:DD:EE:FF", "WEP", -85),
                WifiNetwork("Hidden_Network", "99:88:77:66:55:44", "WPA2", -70)
            )
            isScanning = false
        }
    }

    LaunchedEffect(isAttacking) {
        if (isAttacking && selectedNetwork != null) {
            attackStatus = ""
            handshakeVisualPhase = HandshakeVisualPhase.Prepare.ordinal

            attackStatus += "[*] Targeting BSSID: ${selectedNetwork!!.bssid}\n"
            delay(800)
            attackStatus += "[*] Channel hopping... locked on CH 6\n"
            delay(800)

            handshakeVisualPhase = HandshakeVisualPhase.Deauth.ordinal
            attackStatus += "[*] Sending Deauth packets to broadcast (ff:ff:ff:ff:ff:ff)...\n"
            delay(2_400)

            handshakeVisualPhase = HandshakeVisualPhase.Disconnected.ordinal
            attackStatus += "[*] Waiting for client to reconnect...\n"
            delay(800)

            handshakeVisualPhase = HandshakeVisualPhase.Handshake.ordinal
            delay(3_000)

            handshakeVisualPhase = HandshakeVisualPhase.Captured.ordinal
            attackStatus += "[+] WPA Handshake captured! (WPA_Handshake.cap)\n"
            delay(800)
            attackStatus += "[*] Ready for offline dictionary attack.\n"
            delay(600)

            isAttacking = false
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.appTopBarTint(),
                title = {
                    Column {
                        Text("Wireless lab", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Created by Harshal Mahadik",
                            color = TextTertiary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = TextPrimary
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { isScanning = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .neonGlow(corner = 16.dp, neonColor = NeonPurple),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple.copy(alpha = 0.15f),
                    contentColor = NeonPurple
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, NeonPurple)
            ) {
                Icon(if (isScanning) Icons.Default.WifiOff else Icons.Default.Wifi, contentDescription = null, tint = NeonPurple)
                Spacer(Modifier.width(8.dp))
                Text(if (isScanning) "Scanning…" else "Scan networks", fontWeight = FontWeight.Bold, color = NeonPurple)
            }

            if (isScanning) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    WifiRadarScanner()
                }
            } else if (networks.isNotEmpty()) {
                Text("Detected access points", color = TextTertiary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .glassCard(corner = 18.dp, neonColor = NeonCyan)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(networks) { network ->
                        val isSelected = selectedNetwork == network
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                                .border(
                                    2.dp,
                                    if (isSelected) NeonPurple else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedNetwork = network }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(network.ssid, color = NeonCyan, fontWeight = FontWeight.Bold)
                                Text("BSSID: ${network.bssid}", color = NeonCyan.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(network.security, color = if (network.security == "WEP") SemanticDanger else SemanticWarning, fontSize = 12.sp)
                                Text("${network.signal} dBm", color = NeonCyan, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            AnimatedVisibility(visible = selectedNetwork != null) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (attackStatus.isNotEmpty() || isAttacking) {
                        WpaHandshakeVisualizer(
                            phaseOrdinal = handshakeVisualPhase,
                            isAttacking = isAttacking,
                            attackStatus = attackStatus
                        )
                    }

                    Button(
                        onClick = { isAttacking = true },
                        enabled = !isAttacking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .neonGlow(corner = 16.dp, neonColor = SemanticDanger),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SemanticDanger.copy(alpha = 0.15f),
                            contentColor = SemanticDanger,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, SemanticDanger)
                    ) {
                        Text(
                            if (isAttacking) "Running simulation…" else "Capture handshake (deauth demo)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WifiRadarScanner() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )

    androidx.compose.foundation.Canvas(modifier = Modifier.size(200.dp)) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2
        
        // Draw grid lines
        drawLine(NeonCyan.copy(alpha = 0.2f), androidx.compose.ui.geometry.Offset(0f, center.y), androidx.compose.ui.geometry.Offset(size.width, center.y), 1f)
        drawLine(NeonCyan.copy(alpha = 0.2f), androidx.compose.ui.geometry.Offset(center.x, 0f), androidx.compose.ui.geometry.Offset(center.x, size.height), 1f)
        
        // Draw concentric circles
        drawCircle(NeonCyan.copy(alpha = 0.2f), radius = radius * 0.33f, center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
        drawCircle(NeonCyan.copy(alpha = 0.2f), radius = radius * 0.66f, center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
        drawCircle(NeonCyan.copy(alpha = 0.5f), radius = radius, center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))

        // Draw sweeping arc
        drawArc(
            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.5f), NeonCyan),
                center = center
            ),
            startAngle = rotation - 60f,
            sweepAngle = 60f,
            useCenter = true,
            topLeft = androidx.compose.ui.geometry.Offset.Zero,
            size = androidx.compose.ui.geometry.Size(size.width, size.height)
        )
    }
}

@Composable
fun WpaHandshakeVisualizer(phaseOrdinal: Int, isAttacking: Boolean, attackStatus: String) {
    val phase = HandshakeVisualPhase.entries.getOrElse(phaseOrdinal) { HandshakeVisualPhase.Prepare }
    val deauthSweep = rememberInfiniteTransition(label = "deauthSweep")
    val deauthProgress by deauthSweep.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "deauthProgress"
    )

    val hsPulse = rememberInfiniteTransition(label = "hsPulse")
    val hsPhase by hsPulse.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hsPhaseFloat"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .border(2.dp, NeonCyan, RoundedCornerShape(20.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            when (phase) {
                HandshakeVisualPhase.Prepare -> "Link up — sniffing channel"
                HandshakeVisualPhase.Deauth -> "Injecting spoofed DEAUTH frames"
                HandshakeVisualPhase.Disconnected -> "Client dropped — waiting for WPA reconnect"
                HandshakeVisualPhase.Handshake -> "4-way handshake visible on-air"
                HandshakeVisualPhase.Captured -> "EAPOL capture complete — PCAP ready"
            },
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val routerPos = Offset(size.width * 0.14f, size.height * 0.42f)
            val clientPos = Offset(size.width * 0.86f, size.height * 0.42f)
            val attackerPos = Offset(size.width * 0.5f, size.height * 0.82f)

            fun drawNode(center: Offset, color: androidx.compose.ui.graphics.Color) {
                drawRoundRect(
                    color,
                    topLeft = Offset(center.x - 22f, center.y - 18f),
                    size = Size(44f, 36f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 2.5f)
                )
                drawRoundRect(color.copy(alpha = 0.35f), Offset(center.x - 22f, center.y - 18f), Size(44f, 36f), CornerRadius(10f, 10f))
            }

            drawNode(routerPos, Color.Gray)
            val clientTint = when (phase) {
                HandshakeVisualPhase.Disconnected -> NeonCyan.copy(alpha = 0.35f)
                else -> NeonCyan
            }
            drawNode(clientPos, clientTint)
            drawNode(attackerPos, SemanticDanger)

            val solidStroke = Stroke(width = 5f)

            fun drawBrokenLink(ap: Offset, sta: Offset, baseColor: Color) {
                val mid = (ap.x + sta.x) / 2
                drawLine(baseColor.copy(alpha = 0.45f), ap, Offset(mid - 18f, ap.y), strokeWidth = solidStroke.width)
                drawLine(baseColor.copy(alpha = 0.45f), Offset(mid + 18f, ap.y), sta, strokeWidth = solidStroke.width)
                drawCircle(SemanticDanger.copy(alpha = 0.9f), radius = 5f, center = Offset(mid, ap.y))
            }

            when (phase) {
                HandshakeVisualPhase.Prepare ->
                    drawLine(Color.Gray, routerPos, clientPos, solidStroke.width)

                HandshakeVisualPhase.Deauth -> {
                    drawLine(
                        Color.Gray.copy(alpha = 0.35f),
                        routerPos,
                        clientPos,
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                    )
                    drawLine(SemanticDanger.copy(alpha = 0.45f), attackerPos, clientPos, strokeWidth = 2.5f)
                }

                HandshakeVisualPhase.Disconnected ->
                    drawBrokenLink(routerPos, clientPos, NeonCyan)

                HandshakeVisualPhase.Handshake -> {
                    drawLine(NeonCyan.copy(alpha = 0.82f), routerPos, clientPos, strokeWidth = 5f)
                    val cycle = hsPhase % 4f
                    val idx = cycle.toInt().coerceIn(0, 3)
                    val frac = cycle - idx
                    fun pointOnLink(t: Float) = Offset(
                        routerPos.x + (clientPos.x - routerPos.x) * t,
                        routerPos.y + (clientPos.y - routerPos.y) * t
                    )
                    val t = if (idx % 2 == 0) frac else 1f - frac
                    val col = if (idx % 2 == 0) NeonCyan else NeonPink
                    val p = pointOnLink(t.coerceIn(0f, 1f))
                    drawCircle(col, radius = 8f, center = p)
                    drawCircle(col.copy(alpha = 0.42f), radius = 14f, center = p)
                }

                HandshakeVisualPhase.Captured -> {
                    drawLine(NeonBlue.copy(alpha = 0.55f), routerPos, clientPos, strokeWidth = 5f)
                    fun pointOnLink(t: Float) = Offset(
                        routerPos.x + (clientPos.x - routerPos.x) * t,
                        routerPos.y + (clientPos.y - routerPos.y) * t
                    )
                    listOf(0.18f, 0.38f, 0.58f, 0.78f).forEach { t ->
                        drawCircle(SemanticSuccess.copy(alpha = 0.95f), radius = 7f, center = pointOnLink(t))
                        drawCircle(SemanticSuccess.copy(alpha = 0.22f), radius = 15f, center = pointOnLink(t))
                    }
                    drawLine(
                        SemanticSuccess.copy(alpha = 0.35f),
                        attackerPos,
                        routerPos,
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                    )
                    val tap = 0.78f
                    val lx = attackerPos.x + (routerPos.x - attackerPos.x) * tap
                    val ly = attackerPos.y + (routerPos.y - attackerPos.y) * tap
                    drawCircle(SemanticSuccess, radius = 12f, center = Offset(lx, ly))
                    drawCircle(SemanticSuccess.copy(alpha = 0.3f), radius = 24f, center = Offset(lx, ly))
                }
            }

            if (phase == HandshakeVisualPhase.Deauth) {
                listOf(deauthProgress, (deauthProgress + 0.33f) % 1f, (deauthProgress + 0.66f) % 1f).forEachIndexed { _, t ->
                    val tp = Offset(
                        attackerPos.x + (clientPos.x - attackerPos.x) * t,
                        attackerPos.y + (clientPos.y - attackerPos.y) * t
                    )
                    drawCircle(SemanticDanger, radius = 7f, center = tp)
                    drawCircle(SemanticDanger.copy(alpha = 0.35f), radius = 16f, center = tp)
                }
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot("Router (AP)", Color.Gray)
            LegendDot("You", SemanticDanger)
            LegendDot("Client", NeonCyan)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.95f))
                .padding(12.dp)
        ) {
            Text(
                text = attackStatus.ifEmpty { if (isAttacking) "…\n" else "" },
                color = NeonCyan.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color)
            )
            Text(label, color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}
