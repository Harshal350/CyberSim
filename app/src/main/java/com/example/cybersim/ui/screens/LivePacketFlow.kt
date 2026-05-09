package com.example.cybersim.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.cybersim.ui.theme.NeonBlue
import com.example.cybersim.ui.theme.NeonCyan
import com.example.cybersim.ui.theme.NeonGreen
import com.example.cybersim.ui.theme.NeonPurple
import com.example.cybersim.ui.theme.SemanticDanger
import com.example.cybersim.ui.theme.SemanticSuccess
import com.example.cybersim.ui.theme.TextPrimary
import com.example.cybersim.ui.theme.TextTertiary
import com.example.cybersim.ui.theme.glassCard
import kotlinx.coroutines.delay
import kotlin.random.Random

data class NetworkNode(val id: String, var position: Offset, val isTarget: Boolean = false)
data class Packet(var id: Int, var startNode: NetworkNode, var endNode: NetworkNode, var progress: Float, val isMalicious: Boolean)

@Composable
fun LivePacketFlow(threatLevel: String) {
    val isCritical = threatLevel == "HIGH" || threatLevel == "CRITICAL"
    
    // Animate base color based on threat
    val activeColor by animateColorAsState(
        targetValue = if (isCritical) SemanticDanger else NeonBlue,
        animationSpec = tween(durationMillis = 500),
        label = "radarColor"
    )

    // Pulse transition for critical mode
    val infiniteTransition = rememberInfiniteTransition(label = "criticalPulse")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isCritical) 20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseSize"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // State for nodes and packets
    var width by remember { mutableFloatStateOf(0f) }
    var height by remember { mutableFloatStateOf(0f) }

    val nodes = remember { mutableStateListOf<NetworkNode>() }
    val packets = remember { mutableStateListOf<Packet>() }
    var packetCounter by remember { mutableIntStateOf(0) }
    
    // Explicit frame trigger to ensure smooth redraws even at low threat levels
    var frameTrigger by remember { mutableLongStateOf(0L) }

    // Initialize nodes when size is known
    LaunchedEffect(width, height) {
        if (width > 0 && height > 0 && nodes.isEmpty()) {
            nodes.add(NetworkNode("Gateway", Offset(width / 2, height / 2), isTarget = true))
            nodes.add(NetworkNode("DB_Server", Offset(width * 0.15f, height * 0.25f)))
            nodes.add(NetworkNode("Auth_Node", Offset(width * 0.85f, height * 0.35f)))
            nodes.add(NetworkNode("Web_Front", Offset(width * 0.25f, height * 0.75f)))
            nodes.add(NetworkNode("Ext_Client", Offset(width * 0.85f, height * 0.85f)))
        }
    }

    // Spawn packets continuously
    LaunchedEffect(threatLevel, nodes.size) {
        if (nodes.size < 2) return@LaunchedEffect
        while (true) {
            val spawnRate = if (isCritical) (50L..200L) else (500L..1500L)
            delay(spawnRate.random())

            val start = nodes.random()
            var end = nodes.random()
            while (start == end) end = nodes.random()

            // During critical, more packets target the gateway and are marked malicious
            val malicious = if (isCritical) Random.nextFloat() < 0.7f else Random.nextFloat() < 0.1f
            if (isCritical && Random.nextFloat() < 0.6f) end = nodes[0] // Route to Gateway

            packets.add(Packet(packetCounter++, start, end, 0f, malicious))
        }
    }

    // Animate packet progress smoothly using frame-time delta
    LaunchedEffect(isCritical) {
        var lastTimeMillis = withFrameMillis { it }
        while (true) {
            withFrameMillis { currentTimeMillis ->
                val dt = (currentTimeMillis - lastTimeMillis) / 1000f
                lastTimeMillis = currentTimeMillis
                
                // Slightly faster base speed for smoother feel at low threat
                val speed = if (isCritical) 1.6f else 0.75f 
                
                if (packets.isNotEmpty()) {
                    packets.forEach { it.progress += speed * dt }
                    packets.removeAll { it.progress >= 1f }
                }
                
                // Force recomposition to ensure Canvas draws every single frame
                frameTrigger = currentTimeMillis
            }
        }
    }

    var selectedNode by remember { mutableStateOf<NetworkNode?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .glassCard(20.dp)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = "Live topology",
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 0.6.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "CLICK ON NODES FOR MORE INFO",
                    color = NeonCyan,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            if (selectedNode != null) {
                Text(
                    text = "${selectedNode!!.id} · tap to clear",
                    color = NeonCyan,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { selectedNode = null }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            var clicked = false
                            for (node in nodes) {
                                // Simple hit detection (distance < radius + padding)
                                val dist = kotlin.math.hypot(
                                    (tapOffset.x - node.position.x).toDouble(),
                                    (tapOffset.y - node.position.y).toDouble()
                                )
                                if (dist < 30f) {
                                    selectedNode = if (selectedNode == node) null else node
                                    clicked = true
                                    break
                                }
                            }
                            if (!clicked) selectedNode = null
                        }
                    }
            ) {
                // Observe frameTrigger to force redraw every frame for fluid animation
                val _trigger = frameTrigger

                width = size.width
                height = size.height

                if (nodes.isEmpty()) return@Canvas

                // Draw connection lines
                nodes.forEach { n1 ->
                    nodes.forEach { n2 ->
                        if (n1 != n2) {
                            drawLine(
                                color = activeColor.copy(alpha = 0.15f),
                                start = n1.position,
                                end = n2.position,
                                strokeWidth = 2f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Draw packets
                packets.forEach { packet ->
                    val currentX = packet.startNode.position.x + (packet.endNode.position.x - packet.startNode.position.x) * packet.progress
                    val currentY = packet.startNode.position.y + (packet.endNode.position.y - packet.startNode.position.y) * packet.progress
                    val packetColor = if (packet.isMalicious) SemanticDanger else NeonCyan
                    
                    drawCircle(
                        color = packetColor,
                        radius = 6f,
                        center = Offset(currentX, currentY)
                    )
                }

                // Draw nodes
                nodes.forEach { node ->
                    val isSelected = node == selectedNode
                    val nodeColor = if (isSelected) NeonPurple else if (node.isTarget) activeColor else Color.DarkGray
                    
                    if (node.isTarget && isCritical && pulseSize > 0f) {
                        drawCircle(
                            color = nodeColor.copy(alpha = pulseAlpha),
                            radius = 16f + pulseSize,
                            center = node.position
                        )
                    }

                    if (isSelected) {
                        drawCircle(
                            color = NeonPurple.copy(alpha = 0.22f),
                            radius = 24f,
                            center = node.position
                        )
                    }

                    drawCircle(
                        color = nodeColor,
                        radius = if (node.isTarget) 16f else 12f,
                        center = node.position
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = if (node.isTarget) 12f else 8f,
                        center = node.position
                    )
                }
            }

            // Node Data Overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = selectedNode != null,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                selectedNode?.let { node ->
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .border(2.dp, NeonCyan, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Node · ${node.id}", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            val cpu = if (isCritical && node.isTarget) (85..99).random() else (10..40).random()
                            val conns = if (isCritical && node.isTarget) (1000..5000).random() else (50..200).random()
                            Text("CPU load · $cpu%", color = if (cpu > 80) SemanticDanger else NeonGreen, fontSize = 10.sp)
                            Text("Connections · $conns", color = NeonCyan, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
