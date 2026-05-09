package com.example.cybersim.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SecurityLog(val timestamp: String, val level: String, val message: String)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val _activeConnections = MutableStateFlow("0")
    val activeConnections: StateFlow<String> = _activeConnections.asStateFlow()

    private val _packetsSniffed = MutableStateFlow("0.0")
    val packetsSniffed: StateFlow<String> = _packetsSniffed.asStateFlow()

    private val _threatLevel = MutableStateFlow("LOW")
    val threatLevel: StateFlow<String> = _threatLevel.asStateFlow()

    private val _liveLogs = MutableStateFlow<List<SecurityLog>>(emptyList())
    val liveLogs: StateFlow<List<SecurityLog>> = _liveLogs.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    init {
        connectToWebSocket()
        startSimulatedLogs()
    }

    private fun startSimulatedLogs() {
        viewModelScope.launch {
            val baseLogs = listOf(
                Pair("INFO", "DNS query received from 192.168.1.45"),
                Pair("INFO", "TCP Handshake successful (Port 443)"),
                Pair("INFO", "Heartbeat sync complete"),
                Pair("WARNING", "Unusual packet detected on wlan0"),
                Pair("WARNING", "High latency on route 10.0.0.1"),
                Pair("ALERT", "Multiple failed login attempts via SSH"),
                Pair("CRITICAL", "Unauthorized access attempt blocked!"),
                Pair("CRITICAL", "Intrusion detected: Signature Match [CVE-2021-44228]"),
                Pair("CRITICAL", "DDoS mitigation active on interface eth0")
            )

            var packetCount = 0.0

            while (true) {
                // Determine speed and severity based on current threat level
                val currentThreat = _threatLevel.value
                val delayMs = when (currentThreat) {
                    "LOW" -> (1000..3000).random().toLong()
                    "MEDIUM" -> (500..1500).random().toLong()
                    "HIGH" -> (200..600).random().toLong()
                    "CRITICAL" -> (50..200).random().toLong()
                    else -> 2000L
                }

                delay(delayMs)

                // Pick a log that makes sense for the threat level
                val logPool = when (currentThreat) {
                    "CRITICAL" -> baseLogs
                    "HIGH" -> baseLogs.filter { it.first != "CRITICAL" }
                    "MEDIUM" -> baseLogs.filter { it.first == "INFO" || it.first == "WARNING" }
                    else -> baseLogs.filter { it.first == "INFO" }
                }

                val chosen = logPool.random()
                addLog(chosen.first, chosen.second)

                // Simulate other metrics moving
                _activeConnections.value = ((50..500).random() * (if (currentThreat == "CRITICAL") 10 else 1)).toString()
                packetCount += if (currentThreat == "CRITICAL") (50..200).random() / 10.0 else (1..10).random() / 10.0
                _packetsSniffed.value = String.format("%.1f", packetCount)
            }
        }

        // Randomly trigger attacks to simulate a dynamic environment
        viewModelScope.launch {
            while (true) {
                delay((10000..20000).random().toLong()) // Every 10-20 seconds
                _threatLevel.value = listOf("MEDIUM", "HIGH", "CRITICAL").random()
                
                // Keep the attack going for a bit
                delay((5000..15000).random().toLong())
                _threatLevel.value = "LOW"
            }
        }
    }

    private fun addLog(level: String, message: String) {
        val newLog = SecurityLog(
            timestamp = timeFormat.format(Date()),
            level = level,
            message = message
        )
        val currentList = _liveLogs.value.toMutableList()
        currentList.add(newLog)
        if (currentList.size > 50) {
            currentList.removeAt(0) // Keep the last 50 logs to prevent memory issues
        }
        _liveLogs.value = currentList
    }

    private fun connectToWebSocket() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8000/ws/dashboard")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    // If the backend sends real data, we can override the simulation
                    if (json.has("active_connections")) _activeConnections.value = json.getString("active_connections")
                    if (json.has("packets_sniffed")) _packetsSniffed.value = json.getString("packets_sniffed")
                    if (json.has("threat_level")) _threatLevel.value = json.getString("threat_level")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                // Let the simulated logs handle things if disconnected
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "ViewModel cleared")
    }
}
