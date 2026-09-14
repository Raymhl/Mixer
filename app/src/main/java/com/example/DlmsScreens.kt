package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MagentaAccent
import com.example.ui.theme.SurfaceDark

@Composable
fun DlmsApp(viewModel: DlmsViewModel) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = CyanPrimary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Tune, contentDescription = "Mixer") },
                    label = { Text("Mixer") },
                    selected = currentRoute == "mixer",
                    onClick = { navController.navigate("mixer") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Waves, contentDescription = "Crossover") },
                    label = { Text("X-Over") },
                    selected = currentRoute == "xover",
                    onClick = { navController.navigate("xover") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AvTimer, contentDescription = "Delay") },
                    label = { Text("Delay") },
                    selected = currentRoute == "delay",
                    onClick = { navController.navigate("delay") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Assist") },
                    label = { Text("AI Assist") },
                    selected = currentRoute == "ai",
                    onClick = { navController.navigate("ai") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "mixer",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("mixer") { MixerScreen(state, viewModel) }
            composable("xover") { CrossoverScreen(state, viewModel) }
            composable("delay") { DelayScreen(state, viewModel) }
            composable("ai") { AiAssistantScreen(state, viewModel) }
        }
    }
}

@Composable
fun MixerScreen(state: DlmsState, viewModel: DlmsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Text("DIGITAL MIXER", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FaderChannel(state.inputLeft, viewModel)
            FaderChannel(state.inputRight, viewModel)
            Spacer(modifier = Modifier.width(16.dp))
            FaderChannel(state.outMainLeft, viewModel)
            FaderChannel(state.outMainRight, viewModel)
            FaderChannel(state.outSub, viewModel, color = MagentaAccent)
        }
    }
}

@Composable
fun FaderChannel(channel: ChannelState, viewModel: DlmsViewModel, color: Color = CyanPrimary) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(channel.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(String.format("%.1f dB", channel.gainDb), color = color, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Fader placeholder using Slider for simplicity, vertically
        Box(
            modifier = Modifier
                .height(200.dp)
                .width(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Very simple vertical fader simulation
            val fillHeight = (channel.gainDb + 60f) / 72f * 200f // range -60 to +12
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(fillHeight.dp.coerceIn(0.dp, 200.dp))
                    .background(color)
            )
            
            // Allow clicking to set gain roughly
            Box(
                modifier = Modifier.fillMaxSize().clickable { /* complex drag logic omitted for mockup */ }
            )
            
            Slider(
                value = channel.gainDb,
                onValueChange = { viewModel.updateGain(channel.name, it) },
                valueRange = -60f..12f,
                modifier = Modifier
                    .requiredSize(200.dp, 40.dp)
                    .graphicsLayer {
                        rotationZ = -90f
                    },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.toggleMute(channel.name) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (channel.mute) MagentaAccent else SurfaceDark
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("M", color = Color.White)
        }
    }
}

@Composable
fun CrossoverScreen(state: DlmsState, viewModel: DlmsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Text("CROSSOVER / EQ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        Spacer(modifier = Modifier.height(24.dp))
        
        CrossoverControl("Main L", state.outMainLeft, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        CrossoverControl("Main R", state.outMainRight, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        CrossoverControl("Subwoofer", state.outSub, viewModel, isSub = true)
    }
}

@Composable
fun CrossoverControl(title: String, channel: ChannelState, viewModel: DlmsViewModel, isSub: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = if(isSub) MagentaAccent else CyanPrimary, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("HPF: ${channel.hpfHz.toInt()} Hz", color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = channel.hpfHz,
                        onValueChange = { viewModel.updateCrossover(channel.name, it, null) },
                        valueRange = 20f..5000f,
                        colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("LPF: ${channel.lpfHz.toInt()} Hz", color = Color.White, fontSize = 12.sp)
                    Slider(
                        value = channel.lpfHz,
                        onValueChange = { viewModel.updateCrossover(channel.name, null, it) },
                        valueRange = if(isSub) 40f..500f else 2000f..20000f,
                        colors = SliderDefaults.colors(thumbColor = MagentaAccent, activeTrackColor = MagentaAccent)
                    )
                }
            }
        }
    }
}

@Composable
fun DelayScreen(state: DlmsState, viewModel: DlmsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Text("TIME ALIGNMENT", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Speed of sound approx 343 m/s", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(24.dp))
        
        DelayControl("Main L", state.outMainLeft, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        DelayControl("Main R", state.outMainRight, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        DelayControl("Subwoofer", state.outSub, viewModel)
    }
}

@Composable
fun DelayControl(title: String, channel: ChannelState, viewModel: DlmsViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = CyanPrimary, fontWeight = FontWeight.Bold)
                val distance = channel.delayMs * 0.343f // meters
                Text(String.format("%.2f meters", distance), color = Color.Gray, fontSize = 12.sp)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.updateDelay(channel.name, (channel.delayMs - 1f).coerceAtLeast(0f)) }) {
                    Icon(Icons.Filled.Remove, "Decrease", tint = Color.White)
                }
                
                Text(String.format("%.1f ms", channel.delayMs), color = Color.White, fontSize = 16.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                
                IconButton(onClick = { viewModel.updateDelay(channel.name, (channel.delayMs + 1f).coerceAtMost(200f)) }) {
                    Icon(Icons.Filled.Add, "Increase", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun AiAssistantScreen(state: DlmsState, viewModel: DlmsViewModel) {
    var inputText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("AI AUDIO ASSISTANT", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        }
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.chatMessages) { msg ->
                ChatBubble(msg)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (state.isAiThinking) {
                item {
                    Text("AI is analyzing...", color = MagentaAccent, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about setup, delays, EQ...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    viewModel.sendMessageToAi(inputText)
                    inputText = ""
                },
                containerColor = CyanPrimary,
                contentColor = Color.Black,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val align = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (msg.isUser) CyanPrimary else SurfaceDark
    val textColor = if (msg.isUser) Color.Black else Color.White
    val shape = if (msg.isUser) 
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp) 
    else 
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bgColor)
                .padding(12.dp)
        ) {
            Text(msg.text, color = textColor, fontSize = 14.sp)
        }
    }
}
