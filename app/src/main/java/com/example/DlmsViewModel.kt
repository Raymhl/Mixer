package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChannelState(
    val name: String,
    val gainDb: Float = 0f,
    val mute: Boolean = false,
    val delayMs: Float = 0f,
    val hpfHz: Float = 20f,
    val lpfHz: Float = 20000f,
    val phaseInvert: Boolean = false
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class DlmsState(
    val inputLeft: ChannelState = ChannelState("In L"),
    val inputRight: ChannelState = ChannelState("In R"),
    val outMainLeft: ChannelState = ChannelState("Out Main L"),
    val outMainRight: ChannelState = ChannelState("Out Main R"),
    val outSub: ChannelState = ChannelState("Out Sub", lpfHz = 120f),
    
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage("Welcome to DLMS Pro Setup Assistant. How can I help you tune your system today?", false)
    ),
    val isAiThinking: Boolean = false
)

class DlmsViewModel : ViewModel() {
    private val _state = MutableStateFlow(DlmsState())
    val state: StateFlow<DlmsState> = _state.asStateFlow()

    fun updateGain(channelName: String, newGain: Float) {
        _state.update { current ->
            when (channelName) {
                "In L" -> current.copy(inputLeft = current.inputLeft.copy(gainDb = newGain))
                "In R" -> current.copy(inputRight = current.inputRight.copy(gainDb = newGain))
                "Out Main L" -> current.copy(outMainLeft = current.outMainLeft.copy(gainDb = newGain))
                "Out Main R" -> current.copy(outMainRight = current.outMainRight.copy(gainDb = newGain))
                "Out Sub" -> current.copy(outSub = current.outSub.copy(gainDb = newGain))
                else -> current
            }
        }
    }
    
    fun updateDelay(channelName: String, newDelay: Float) {
        _state.update { current ->
            when (channelName) {
                "Out Main L" -> current.copy(outMainLeft = current.outMainLeft.copy(delayMs = newDelay))
                "Out Main R" -> current.copy(outMainRight = current.outMainRight.copy(delayMs = newDelay))
                "Out Sub" -> current.copy(outSub = current.outSub.copy(delayMs = newDelay))
                else -> current
            }
        }
    }

    fun updateCrossover(channelName: String, hpf: Float?, lpf: Float?) {
        _state.update { current ->
            when (channelName) {
                "Out Main L" -> current.copy(outMainLeft = current.outMainLeft.copy(
                    hpfHz = hpf ?: current.outMainLeft.hpfHz,
                    lpfHz = lpf ?: current.outMainLeft.lpfHz
                ))
                "Out Main R" -> current.copy(outMainRight = current.outMainRight.copy(
                    hpfHz = hpf ?: current.outMainRight.hpfHz,
                    lpfHz = lpf ?: current.outMainRight.lpfHz
                ))
                "Out Sub" -> current.copy(outSub = current.outSub.copy(
                    hpfHz = hpf ?: current.outSub.hpfHz,
                    lpfHz = lpf ?: current.outSub.lpfHz
                ))
                else -> current
            }
        }
    }

    fun toggleMute(channelName: String) {
        _state.update { current ->
            when (channelName) {
                "In L" -> current.copy(inputLeft = current.inputLeft.copy(mute = !current.inputLeft.mute))
                "In R" -> current.copy(inputRight = current.inputRight.copy(mute = !current.inputRight.mute))
                "Out Main L" -> current.copy(outMainLeft = current.outMainLeft.copy(mute = !current.outMainLeft.mute))
                "Out Main R" -> current.copy(outMainRight = current.outMainRight.copy(mute = !current.outMainRight.mute))
                "Out Sub" -> current.copy(outSub = current.outSub.copy(mute = !current.outSub.mute))
                else -> current
            }
        }
    }

    fun sendMessageToAi(message: String) {
        if (message.isBlank()) return
        
        _state.update { 
            it.copy(
                chatMessages = it.chatMessages + ChatMessage(message, true),
                isAiThinking = true
            )
        }

        viewModelScope.launch {
            val systemPrompt = """
                You are a professional audio engineer and DLMS (Digital Loudspeaker Management System) assistant.
                You are helping a user tune their audio system. 
                Keep answers concise, technical, and practical. 
                You can advise on delay times (speed of sound ~343m/s), crossovers for subs vs tops, EQ cuts for feedback, etc.
            """.trimIndent()
            
            val reply = askGemini(message, systemPrompt)
            
            _state.update { 
                it.copy(
                    chatMessages = it.chatMessages + ChatMessage(reply, false),
                    isAiThinking = false
                )
            }
        }
    }
}
