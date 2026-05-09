package com.example.cybersim.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.cybersim.ui.theme.AppSurfaceElevated
import com.example.cybersim.ui.theme.GlassStroke
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhishingLabScreen(onNavigateBack: () -> Unit) {
    var showIndicators by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Web Phishing", "Email Analyzer")

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.appTopBarTint(),
                title = {
                    Column {
                        Text(
                            "Phishing intelligence",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Created by Harshal Mahadik",
                            color = TextTertiary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextSecondary
                        )
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
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = NeonCyan.copy(alpha = 0.7f),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonPurple
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            selectedTabIndex = index
                            showIndicators = false 
                        },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) NeonPurple else NeonCyan.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (selectedTabIndex == 0) {
                    WebPhishingAnalyzer()
                } else {
                    EmailPhishingContent()
                }
            }
        }
    }
}

@Composable
fun ColumnScope.EmailPhishingContent() {
    var emailBody by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<List<String>?>(null) }
    
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            analysisResult = null
            kotlinx.coroutines.delay(1000)
            val results = mutableListOf<String>()
            
            if (emailBody.contains("urgent", ignoreCase = true) || emailBody.contains("immediate", ignoreCase = true)) {
                results.add("⚠️ Urgent Language Detected (High Pressure Tactic)")
            }
            if (emailBody.contains("http://") && !emailBody.contains("https://")) {
                results.add("⚠️ Unsecure HTTP Link Detected")
            }
            if (emailBody.contains("password", ignoreCase = true) || emailBody.contains("verify", ignoreCase = true)) {
                results.add("⚠️ Requests Sensitive Information")
            }
            if (results.isEmpty() && emailBody.isNotBlank()) {
                results.add("✅ No obvious heuristic indicators found, but stay vigilant.")
            } else if (emailBody.isBlank()) {
                results.add("❌ Please paste an email body to analyze.")
            }
            
            analysisResult = results
            isAnalyzing = false
        }
    }

    Text(
        text = "Paste a suspicious email below for heuristic analysis.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary
    )

    OutlinedTextField(
        value = emailBody,
        onValueChange = { emailBody = it },
        label = { Text("Email Content") },
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = GlassStroke,
            focusedTextColor = NeonCyan,
            unfocusedTextColor = NeonCyan.copy(alpha = 0.8f)
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
    )

    Spacer(modifier = Modifier.weight(1f))

    if (analysisResult != null) {
        val resColor = if (analysisResult!!.any { it.contains("⚠️") }) SemanticDanger else SemanticSuccess
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(corner = 8.dp, neonColor = resColor)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Heuristic Analysis Results:", color = NeonCyan, fontWeight = FontWeight.Bold)
                analysisResult!!.forEach { result ->
                    Text(result, color = if (result.contains("⚠️")) SemanticDanger else SemanticSuccess, fontSize = 14.sp)
                }
            }
        }
    }

    Button(
        onClick = { isAnalyzing = true },
        enabled = !isAnalyzing,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .neonGlow(corner = 12.dp, neonColor = SemanticDanger),
        colors = ButtonDefaults.buttonColors(
            containerColor = SemanticDanger.copy(alpha = 0.15f),
            contentColor = SemanticDanger,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, SemanticDanger)
    ) {
        Text(
            text = if (isAnalyzing) "ANALYZING HEURISTICS..." else "RUN HEURISTIC SCAN",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun UrlAnalysisFindingCard(text: String) {
    val isWarning = text.contains("⚠️")
    val isError = text.contains("❌")
    val ok = text.contains("✅")
    val borderColor = when {
        isError -> SemanticDanger
        isWarning -> SemanticWarning
        else -> SemanticSuccess
    }
    val iconTint = when {
        isError -> SemanticDanger
        isWarning -> SemanticWarning
        else -> SemanticSuccess
    }
    val icon = when {
        isError -> Icons.Default.Error
        isWarning -> Icons.Default.Warning
        ok -> Icons.Default.CheckCircle
        else -> Icons.Default.Info
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isError || isWarning -> SemanticDanger.copy(alpha = 0.07f)
                    else -> SemanticSuccess.copy(alpha = 0.07f)
                }
            )
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Text(
            text = text,
            color = if (isWarning || isError) NeonCyan else SemanticSuccess,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ColumnScope.WebPhishingAnalyzer() {
    var urlInput by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<List<String>?>(null) }
    var revealedCardCount by remember { mutableIntStateOf(0) }
    
    // Animated Radar Sweep State
    val findingsScroll = rememberScrollState()

    val transition = rememberInfiniteTransition()
    val radarSweep by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarSweep"
    )

    LaunchedEffect(analysisResult) {
        val list = analysisResult ?: return@LaunchedEffect
        revealedCardCount = 0
        repeat(list.size) { i ->
            delay(95)
            revealedCardCount = i + 1
        }
    }

    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            analysisResult = null
            revealedCardCount = 0
            delay(2000) // Simulated scan time
            
            val results = mutableListOf<String>()
            val lowerUrl = urlInput.lowercase()
            
            if (lowerUrl.startsWith("http://")) {
                results.add("⚠️ Missing HTTPS (Connection not secure)")
            }
            if (Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b""").containsMatchIn(lowerUrl)) {
                results.add("⚠️ IP Address used instead of Domain Name")
            }
            if (listOf("login", "update", "secure", "account", "verify", "paypal", "bank").any { lowerUrl.contains(it) }) {
                results.add("⚠️ Contains suspicious social engineering keywords")
            }
            if (lowerUrl.count { it == '.' } > 3) {
                results.add("⚠️ Abnormally long subdomain structure")
            }
            if (results.isEmpty() && urlInput.isNotBlank()) {
                results.add("✅ URL appears syntactically normal, but stay vigilant.")
            } else if (urlInput.isBlank()) {
                results.add("❌ Please enter a URL to analyze.")
            }
            
            analysisResult = results
            isAnalyzing = false
        }
    }

    Text(
        text = "Enter a suspicious URL below to scan for phishing indicators.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary
    )

    OutlinedTextField(
        value = urlInput,
        onValueChange = { urlInput = it },
        label = { Text("Target URL") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = GlassStroke,
            focusedTextColor = NeonCyan,
            unfocusedTextColor = NeonCyan.copy(alpha = 0.8f)
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Fake Browser View to show URL
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .glassCard(corner = 12.dp, neonColor = NeonCyan)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = urlInput.ifEmpty { "http://..." },
                    color = NeonCyan,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                    modifier = Modifier.weight(1f)
                )
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (urlInput.isNotEmpty() && !isAnalyzing && analysisResult == null) {
                    Text("Ready to analyze page content...", color = NeonCyan.copy(alpha = 0.6f))
                }

                if (isAnalyzing) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweepY = size.height / 2 + (size.height * radarSweep)
                        drawLine(
                            color = NeonCyan,
                            start = androidx.compose.ui.geometry.Offset(0f, sweepY),
                            end = androidx.compose.ui.geometry.Offset(size.width, sweepY),
                            strokeWidth = 4f
                        )
                        drawRect(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.3f)),
                                startY = sweepY - 100f,
                                endY = sweepY
                            ),
                            topLeft = androidx.compose.ui.geometry.Offset(0f, sweepY - 100f),
                            size = androidx.compose.ui.geometry.Size(size.width, 100f)
                        )
                    }
                    Text("SCANNING DOMAIN...", color = NeonCyan, style = MaterialTheme.typography.titleLarge)
                }

                analysisResult?.let { findings ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(findingsScroll)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Analysis findings",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        findings.forEachIndexed { index, result ->
                            AnimatedVisibility(
                                visible = index < revealedCardCount,
                                enter = slideInHorizontally(
                                    initialOffsetX = { full -> full },
                                    animationSpec = tween(durationMillis = 320, delayMillis = 0)
                                ) + fadeIn(animationSpec = tween(280))
                            ) {
                                UrlAnalysisFindingCard(text = result)
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { isAnalyzing = true },
        enabled = !isAnalyzing && urlInput.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .neonGlow(corner = 12.dp, neonColor = NeonCyan),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan.copy(alpha = 0.15f),
            contentColor = NeonCyan,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, NeonCyan)
    ) {
        Text(
            text = if (isAnalyzing) "SCANNING..." else "ANALYZE URL",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
