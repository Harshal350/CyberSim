package com.example.cybersim.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor() : ViewModel() {

    private val _terminalHistory = MutableStateFlow<List<String>>(
        listOf("CyberSim OS v3.0 - ADVANCED_CORE", "Type 'help' for available commands.")
    )
    val terminalHistory: StateFlow<List<String>> = _terminalHistory.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    fun processCommand(command: String) {
        if (command.isBlank()) return

        val trimmedCommand = command.trim()

        viewModelScope.launch {
            appendOutput("root@neon:~# $trimmedCommand")

            val parts = trimmedCommand.split(Regex("\\s+"))
            val baseCommand = parts[0].lowercase()

            when (baseCommand) {
                "help" -> appendOutput("Available commands: nmap, aircrack-ng, ifconfig, ping, clear, netstat, traceroute", animate = true)
                "clear" -> _terminalHistory.value = emptyList()
                "ifconfig", "ipconfig" -> showIfconfig()
                "netstat" -> simulateNetstat()
                "traceroute" -> {
                    if (parts.size > 1) simulateTraceroute(parts[1])
                    else appendOutput("Usage: traceroute <target_ip>")
                }
                "ping" -> {
                    if (parts.size > 1) simulatePing(parts[1])
                    else appendOutput("Usage: ping <target_ip>")
                }
                "nmap" -> {
                    if (parts.size > 1) simulateNmap(parts[1])
                    else appendOutput("Usage: nmap <target_ip>")
                }
                "aircrack-ng" -> {
                    if (parts.size > 1) simulateAircrack(parts[1])
                    else appendOutput("Usage: aircrack-ng <capture_file.cap>")
                }
                else -> appendOutput("Command not found: $baseCommand")
            }
        }
    }

    private suspend fun appendOutput(text: String, animate: Boolean = false) {
        if (!animate) {
            _terminalHistory.value = _terminalHistory.value + text
        } else {
            // Typing animation
            _terminalHistory.value = _terminalHistory.value + ""
            for (i in text.indices) {
                val currentList = _terminalHistory.value.toMutableList()
                currentList[currentList.lastIndex] = currentList.last() + text[i]
                _terminalHistory.value = currentList
                delay((5..20).random().toLong()) // Typewriter speed
            }
        }
    }

    private suspend fun showIfconfig() {
        val output = """
            eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
                    inet 192.168.1.105  netmask 255.255.255.0  broadcast 192.168.1.255
                    inet6 fe80::a00:27ff:fe4e:66a  prefixlen 64  scopeid 0x20<link>
                    ether 08:00:27:4e:06:6a  txqueuelen 1000  (Ethernet)
            
            wlan0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
                    inet 10.0.0.42  netmask 255.255.255.0  broadcast 10.0.0.255
                    ether 11:22:33:44:55:66  txqueuelen 1000  (Ethernet)
        """.trimIndent()
        appendOutput(output, animate = true)
    }

    private suspend fun simulatePing(target: String) {
        _isExecuting.value = true
        appendOutput("PING $target (54.239.28.85): 56 data bytes")
        for (i in 1..4) {
            delay(800)
            appendOutput("64 bytes from $target: icmp_seq=$i ttl=115 time=${(10..50).random()} ms")
        }
        appendOutput("--- $target ping statistics ---")
        appendOutput("4 packets transmitted, 4 packets received, 0.0% packet loss")
        _isExecuting.value = false
    }

    private suspend fun simulateNmap(target: String) {
        _isExecuting.value = true
        appendOutput("Starting Nmap 7.92 ( https://nmap.org ) at 2026-05-09 23:42", animate = true)
        delay(1000)
        appendOutput("Nmap scan report for $target", animate = true)
        appendOutput("Host is up (0.042s latency).")
        appendOutput("Not shown: 996 closed tcp ports (reset)")
        delay(1000)
        appendOutput("PORT     STATE SERVICE")
        appendOutput("22/tcp   open  ssh")
        delay(500)
        appendOutput("80/tcp   open  http")
        delay(500)
        appendOutput("443/tcp  open  https")
        delay(500)
        appendOutput("3306/tcp open  mysql")
        delay(1000)
        appendOutput("Nmap done: 1 IP address (1 host up) scanned in 3.42 seconds", animate = true)
        _isExecuting.value = false
    }

    private suspend fun simulateAircrack(file: String) {
        _isExecuting.value = true
        appendOutput("Opening $file", animate = true)
        delay(500)
        appendOutput("Read 1541 packets.")
        delay(500)
        appendOutput("1 potential targets")
        appendOutput("Choosing first network as target.")
        delay(1000)
        appendOutput("Reading wordlist 'rockyou.txt'...", animate = true)
        delay(1500)
        
        for (i in 1..5) {
            appendOutput("Trying keys [ ${(i * 20000)} / 14344391 ] (${(1500..2500).random()} k/s)")
            delay(800)
        }
        
        appendOutput("")
        appendOutput("                                 Aircrack-ng 1.7 ")
        appendOutput("")
        appendOutput("      [00:00:05] 100000/14344391 keys tested (19842.15 k/s) ")
        appendOutput("")
        appendOutput("      KEY FOUND! [ cybersec2026 ]", animate = true)
        appendOutput("")
        appendOutput("      Master Key     : 11 22 33 44 55 66 77 88 99 00 AA BB CC DD EE FF", animate = true)
        appendOutput("      Transient Key  : AA BB CC DD EE FF 11 22 33 44 55 66 77 88 99 00", animate = true)
        
        _isExecuting.value = false
    }

    private suspend fun simulateNetstat() {
        _isExecuting.value = true
        appendOutput("Active Internet connections (w/o servers)", animate = true)
        delay(500)
        appendOutput("Proto Recv-Q Send-Q Local Address           Foreign Address         State")
        val connections = listOf(
            "tcp        0      0 192.168.1.105:ssh     192.168.1.200:54321     ESTABLISHED",
            "tcp        0     64 192.168.1.105:https   10.0.0.42:33412         ESTABLISHED",
            "udp        0      0 192.168.1.105:ntp     216.239.35.0:ntp        ESTABLISHED"
        )
        for (conn in connections) {
            delay(400)
            appendOutput(conn)
        }
        delay(500)
        appendOutput("Active UNIX domain sockets (w/o servers)", animate = true)
        appendOutput("Proto RefCnt Flags       Type       State         I-Node   Path")
        appendOutput("unix  2      [ ]         DGRAM                    12345    /run/systemd/journal/socket")
        _isExecuting.value = false
    }

    private suspend fun simulateTraceroute(target: String) {
        _isExecuting.value = true
        appendOutput("traceroute to $target (54.239.28.85), 30 hops max, 60 byte packets", animate = true)
        delay(800)
        
        val hops = listOf(
            " 1  router.local (192.168.1.1)  2.123 ms  1.845 ms  1.966 ms",
            " 2  10.0.0.1 (10.0.0.1)  14.234 ms  13.987 ms  14.512 ms",
            " 3  isp-gateway.net (68.12.34.56)  24.123 ms  23.845 ms  24.966 ms",
            " 4  * * *",
            " 5  backbone.net (203.0.113.1)  45.123 ms  44.845 ms  46.966 ms",
            " 6  target.server ($target)  55.123 ms  54.845 ms  55.966 ms"
        )
        
        for (hop in hops) {
            delay((600..1200).random().toLong())
            appendOutput(hop)
        }
        
        appendOutput("Trace complete.", animate = true)
        _isExecuting.value = false
    }
}
