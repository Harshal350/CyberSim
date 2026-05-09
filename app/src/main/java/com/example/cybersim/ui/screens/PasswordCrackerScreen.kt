package com.example.cybersim.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
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
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordCrackerScreen(onNavigateBack: () -> Unit) {
    var passwordInput by remember { mutableStateOf("") }
    var isCracking by remember { mutableStateOf(false) }
    var crackedTime by remember { mutableStateOf("") }
    var currentGuess by remember { mutableStateOf("") }

    var dictionary by remember { mutableStateOf("rockyou.txt (14M)") }

    // Calculate Entropy
    val length = passwordInput.length
    var charSpace = 0
    if (passwordInput.any { it.isLowerCase() }) charSpace += 26
    if (passwordInput.any { it.isUpperCase() }) charSpace += 26
    if (passwordInput.any { it.isDigit() }) charSpace += 10
    if (passwordInput.any { !it.isLetterOrDigit() }) charSpace += 32
    
    val entropy = if (length > 0) length * (Math.log(charSpace.toDouble()) / Math.log(2.0)) else 0.0
    
    val strengthColor = when {
        entropy < 28 -> SemanticDanger
        entropy < 40 -> SemanticWarning
        entropy < 60 -> NeonBlue
        else -> NeonPurple
    }
    val strengthText = when {
        entropy == 0.0 -> "NONE"
        entropy < 28 -> "WEAK"
        entropy < 40 -> "MODERATE"
        entropy < 60 -> "STRONG"
        else -> "SECURE"
    }

    // Cracking Simulation Effect
    LaunchedEffect(isCracking) {
        if (isCracking) {
            val totalAttempts = charSpace.toDouble().pow(length.toDouble())
            val attemptsPerSecond = when (dictionary) {
                "fasttrack.txt (220)" -> 10_000_000_000.0 // 10 Billion
                "rockyou.txt (14M)" -> 100_000_000_000.0 // 100 Billion
                "huge_dict.txt (1.4B)" -> 500_000_000_000.0 // 500 Billion
                else -> 100_000_000_000.0
            }
            val secondsToCrack = totalAttempts / attemptsPerSecond

            crackedTime = when {
                secondsToCrack < 1 -> "Instantly"
                secondsToCrack < 60 -> "${secondsToCrack.toInt()} seconds"
                secondsToCrack < 3600 -> "${(secondsToCrack / 60).toInt()} minutes"
                secondsToCrack < 86400 -> "${(secondsToCrack / 3600).toInt()} hours"
                secondsToCrack < 31536000 -> "${(secondsToCrack / 86400).toInt()} days"
                else -> "${(secondsToCrack / 31536000).toInt()} years"
            }

            // Simulate the matrix deciphering process
            val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*"
            val target = passwordInput
            var resolved = ""
            for (i in target.indices) {
                for (j in 0..8) {
                    val randomPart = (i until length).map { charPool.random() }.joinToString("")
                    currentGuess = resolved + randomPart
                    delay(30)
                }
                resolved += target[i]
                currentGuess = resolved + (i + 1 until length).map { charPool.random() }.joinToString("")
            }
            currentGuess = target
            isCracking = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.appTopBarTint(),
                title = {
                    Column {
                        Text(
                            "Password lab",
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
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Enter a password to test its strength against a brute-force dictionary attack.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { 
                    if (!isCracking) passwordInput = it 
                    crackedTime = ""
                    currentGuess = ""
                },
                label = { Text("Target Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = GlassStroke,
                    focusedTextColor = NeonCyan,
                    unfocusedTextColor = NeonCyan.copy(alpha = 0.8f)
                ),
                singleLine = true
            )

            var expanded by remember { mutableStateOf(false) }
            val dictOptions = listOf("fasttrack.txt (220)", "rockyou.txt (14M)", "huge_dict.txt (1.4B)")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = dictionary,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Wordlist Dictionary") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = GlassStroke,
                        focusedTextColor = NeonCyan,
                        unfocusedTextColor = NeonCyan.copy(alpha = 0.8f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.9f))
                ) {
                    dictOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, color = NeonCyan) },
                            onClick = {
                                dictionary = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Entropy Meter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(corner = 20.dp, neonColor = strengthColor)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Entropy: ${String.format("%.1f", entropy)} bits", color = NeonCyan)
                    Text(strengthText, color = strengthColor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (entropy / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = strengthColor,
                    trackColor = Color.Black,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Attack Status
            if (currentGuess.isNotEmpty() || isCracking) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard(corner = 18.dp, neonColor = NeonBlue)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Trying: $currentGuess",
                            color = NeonBlue,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                        if (crackedTime.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Estimated crack time\n$crackedTime",
                                color = SemanticDanger,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { if (passwordInput.isNotEmpty()) isCracking = true },
                enabled = !isCracking && passwordInput.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .neonGlow(corner = 16.dp, neonColor = NeonPurple),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple.copy(alpha = 0.15f),
                    contentColor = NeonPurple,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, NeonPurple)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCracking) "BRUTE-FORCING..." else "INITIATE ATTACK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeonPurple
                )
            }
        }
    }
}
