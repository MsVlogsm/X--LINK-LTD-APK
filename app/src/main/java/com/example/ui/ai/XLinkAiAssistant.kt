package com.example.ui.ai

import android.util.Log
import com.example.data.model.Customer
import com.example.data.model.Language
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout

private const val TAG = "XLinkAiAssistant"

sealed interface AiChatState {
    data object Idle : AiChatState
    data object Loading : AiChatState
    data class Streaming(val partialResponse: String) : AiChatState
    data class Success(val fullResponse: String) : AiChatState
    data class Error(val errorMessage: String) : AiChatState
}

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: String
)

class XLinkAiService {
    // Model initialized lazily as required by Firebase AI Logic
    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val _chatState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val chatState: StateFlow<AiChatState> = _chatState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "init_0",
                sender = "ai",
                text = "হ্যালো! আমি এক্স-লিংক AI নেটওয়ার্ক অ্যাসিস্ট্যান্ট। আপনার ইন্টারনেট সংযোগ, অপটিক্যাল সিগন্যাল, বিলিং বা প্যাকেজ সংক্রান্ত যেকোনো জিজ্ঞাসায় আমি আপনাকে সাহায্য করতে পারি।",
                timestamp = "Just now"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    suspend fun analyzeConnectionTelemetry(
        customer: Customer,
        language: Language
    ) {
        val prompt = if (language == Language.BANGLA) {
            """
            তুমি এক্স-লিংক লিমিটেড (X-Link LTD) আইএসপির সিনিয়র অপটিক্যাল নেটওয়ার্ক ইঞ্জিনিয়ার ও ভার্চুয়াল এআই টেকনিক্যাল অ্যাসিস্ট্যান্ট।
            গ্রাহকের সংযোগের বর্তমান ডেটা:
            - গ্রাহকের নাম: ${customer.name}
            - প্যাকেজ: ${customer.packageName} (${customer.bandwidthMbps} Mbps)
            - অপটিক্যাল RX পাওয়ার: ${customer.opticalPowerDbm} dBm (স্বাভাবিক রেঞ্জ: -15 থেকে -24 dBm)
            - পিং লেটেন্সি: ${customer.pingMs} ms
            - বর্তমান লাইভ ডাউনলোড স্পিড: ${customer.downloadSpeedMbps} Mbps
            - বর্তমান লাইভ আপলোড স্পিড: ${customer.uploadSpeedMbps} Mbps
            - সংযোগ স্থিতি: ${customer.connectionStatus.labelBn}
            - বর্তমান বকেয়া: ৳${customer.balanceDue.toInt()}

            এই ডেটার ভিত্তিতে গ্রাহককে একটি স্পষ্ট, অত্যন্ত তথ্যবহুল ও বন্ধুত্বপূর্ণ বাংলায় টেকনিক্যাল ডায়াগনস্টিক রিপোর্ট দাও।
            রিপোর্টে অপটিক্যাল সিগন্যাল হেলথ, গেমিং/স্ট্রিমিং পারফরম্যান্স, এবং ওয়াইফাই রাউটার অপ্টিমাইজেশনের কিছু বাস্তবসম্মত টিপস অন্তর্ভুক্ত করো।
            """.trimIndent()
        } else {
            """
            You are the Senior Network Support AI Engineer for X-Link LTD Fiber Broadband ISP.
            Analyze the following real-time customer connection telemetry:
            - Customer: ${customer.name} (Code: ${customer.customerCode})
            - Package: ${customer.packageName} (${customer.bandwidthMbps} Mbps)
            - Optical RX Power: ${customer.opticalPowerDbm} dBm (Healthy range: -15 to -24 dBm)
            - Ping Latency: ${customer.pingMs} ms
            - Current Down/Up: ${customer.downloadSpeedMbps} / ${customer.uploadSpeedMbps} Mbps
            - Status: ${customer.connectionStatus.labelEn}
            - Balance Due: ৳${customer.balanceDue.toInt()}

            Provide a concise, professional diagnostic summary evaluating optical fiber health, latency quality for 4K streaming & esports gaming, and practical home Wi-Fi tips.
            """.trimIndent()
        }

        askAi(prompt, userFacingMessage = if (language == Language.BANGLA) "আমার সংযোগ ও সিগন্যাল বিশ্লেষণ করুন" else "Analyze my optical fiber & network health")
    }

    suspend fun sendUserQuery(userQuery: String, customer: Customer, language: Language) {
        val systemContext = """
            You are the intelligent customer care and technical support assistant for X-Link LTD (a premier fiber broadband ISP in Bangladesh).
            Customer info: ${customer.name}, Package: ${customer.packageName}, Due: ৳${customer.balanceDue.toInt()}, Status: ${customer.connectionStatus.name}.
            Help the customer professionally and kindly in ${if (language == Language.BANGLA) "Bangla" else "English"}.
            Query: $userQuery
        """.trimIndent()

        askAi(systemContext, userFacingMessage = userQuery)
    }

    private suspend fun askAi(prompt: String, userFacingMessage: String) {
        val userMsg = ChatMessage(
            id = "msg_" + System.currentTimeMillis(),
            sender = "user",
            text = userFacingMessage,
            timestamp = "Just now"
        )
        _messages.value = _messages.value + userMsg
        _chatState.value = AiChatState.Loading

        val accumulatedText = StringBuilder()
        try {
            // Realistic 60s timeout as mandated by Firebase AI Logic skill
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    accumulatedText.append(chunkText)
                    _chatState.value = AiChatState.Streaming(accumulatedText.toString())
                }
            }

            val finalResponse = accumulatedText.toString()
            if (finalResponse.isNotBlank()) {
                val aiMsg = ChatMessage(
                    id = "ai_" + System.currentTimeMillis(),
                    sender = "ai",
                    text = finalResponse,
                    timestamp = "Just now"
                )
                _messages.value = _messages.value + aiMsg
                _chatState.value = AiChatState.Success(finalResponse)
            } else {
                _chatState.value = AiChatState.Error("Gemini AI returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini generation timed out", e)
            _chatState.value = AiChatState.Error("AI generation timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini AI generation failed", e)
            val errorMsg = e.localizedMessage ?: "Failed to reach Gemini AI via Firebase AI Logic."
            _chatState.value = AiChatState.Error(errorMsg)
        }
    }

    fun resetChat() {
        _chatState.value = AiChatState.Idle
    }
}
