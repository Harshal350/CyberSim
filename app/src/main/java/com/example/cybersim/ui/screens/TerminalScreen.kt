package com.example.cybersim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cybersim.ui.theme.AppCard
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
import kotlinx.coroutines.delay

fun highlightTerminalText(text: String): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        val ipRegex = Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b""")
        ipRegex.findAll(text).forEach {
            addStyle(SpanStyle(color = NeonCyan), it.range.first, it.range.last + 1)
        }
        val portRegex = Regex("""\b\d{1,5}/(tcp|udp)\b""")
        portRegex.findAll(text).forEach {
            addStyle(SpanStyle(color = NeonPink.copy(alpha = 0.95f)), it.range.first, it.range.last + 1)
        }
        val keywords = listOf("open", "KEY FOUND!", "CRITICAL", "Warning", "root@neon:~#")
        keywords.forEach { keyword ->
            var startIndex = text.indexOf(keyword, ignoreCase = true)
            while (startIndex >= 0) {
                val color = when (keyword.uppercase()) {
                    "OPEN" -> SemanticWarning
                    "KEY FOUND!" -> SemanticSuccess
                    "CRITICAL" -> SemanticDanger
                    "WARNING" -> SemanticWarning
                    else -> NeonBlue
                }
                addStyle(SpanStyle(color = color, fontWeight = FontWeight.SemiBold), startIndex, startIndex + keyword.length)
                startIndex = text.indexOf(keyword, startIndex + keyword.length, ignoreCase = true)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    onNavigateBack: () -> Unit,
    viewModel: TerminalViewModel = hiltViewModel()
) {
    var commandInput by remember { mutableStateOf("") }
    val terminalHistory by viewModel.terminalHistory.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(terminalHistory.size, terminalHistory.lastOrNull()?.length) {
        if (terminalHistory.isNotEmpty()) {
            listState.animateScrollToItem(terminalHistory.size - 1)
        }
    }

    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(isExecuting) {
        while (isExecuting) {
            cursorVisible = !cursorVisible
            delay(400)
        }
        cursorVisible = false
    }

    val bodyMono = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = NeonGreen.copy(alpha = 0.9f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.appTopBarTint(),
                title = {
                    Column {
                        Text(
                            "Command console",
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .glassCard(corner = 20.dp, neonColor = NeonGreen)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        terminalHistory,
                        key = { index, line -> "${index}_${line.hashCode()}" }
                    ) { index, line ->
                        val isLastLine = index == terminalHistory.lastIndex
                        val textToRender =
                            if (isExecuting && isLastLine && cursorVisible) "$line▍" else line
                        Text(
                            text = highlightTerminalText(textToRender),
                            style = bodyMono
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(AppCard.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .border(2.dp, NeonPurple, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "› ",
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                BasicTextField(
                    value = commandInput,
                    onValueChange = { if (!isExecuting) commandInput = it },
                    textStyle = TextStyle(
                        color = NeonGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(if (isExecuting) Color.Transparent else NeonPurple),
                    singleLine = true,
                    enabled = !isExecuting,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (commandInput.isNotBlank() && !isExecuting) {
                                viewModel.processCommand(commandInput)
                                commandInput = ""
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
