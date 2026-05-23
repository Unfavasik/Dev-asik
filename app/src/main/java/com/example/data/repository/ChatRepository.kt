package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessage
import com.example.data.local.ChatSession
import com.example.data.remote.GeminiApi
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ChatRepository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "asif_assistant_db"
    ).fallbackToDestructiveMigration()
        .build()

    private val chatDao = db.chatDao()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val geminiApi = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GeminiApi::class.java)

    fun getAllSessions(): Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> =
        chatDao.getMessagesForSession(sessionId)

    suspend fun createSession(id: String, title: String) {
        withContext(Dispatchers.IO) {
            chatDao.insertSession(ChatSession(id, title))
        }
    }

    suspend fun deleteSession(session: ChatSession) {
        withContext(Dispatchers.IO) {
            chatDao.deleteMessagesForSession(session.id)
            chatDao.deleteSession(session)
        }
    }

    suspend fun sendMessage(sessionId: String, userText: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Save user's message locally first
                val userMsg = ChatMessage(sessionId = sessionId, role = "user", content = userText)
                chatDao.insertMessage(userMsg)

                // 2. Fetch all previous messages in session to construct conversation history
                val history = chatDao.getMessagesForSession(sessionId).first().takeLast(30) // Only send last 30 messages to avoid token bloat

                // 3. Map messages to GeminiContent objects
                val contents = history.map { msg ->
                    GeminiContent(
                        parts = listOf(GeminiPart(text = msg.content))
                    )
                }

                // 4. Set prompt system instruction
                val systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = """
                            You are Asif's Personal AI Digital Assistant. Your role is to represent Asif naturally, intelligently, and helpfully in chat conversations.

                            Identity & Persona:
                            - Speak like Asif: natural, friendly, confident, smart, casual, and highly expressive.
                            - Reply in Banglish (Bengali written in English letters) naturally, capturing real colloquial nuances (e.g., "Ki bolcho bhai 😄", "Eta valo idea hote pare", "Aktu explain korle better help korte parbo", "Haha eta interesting 😆"). But you can also reply in clear, friendly English if the user talks to you in English and expects a detailed professional response.
                            - Keep your answers short, crisp, and conversational by default. Do not output lengthy blocks of paragraphs or robotic bullet points unless explicitly asked for content drafts, podcast scripts, or detailed tech help.
                            - Sound emotionally aware, warm, respectful, chill, and enthusiastic.

                            About Asif (Asik Ikbal):
                            - Full Name: Asik Ikbal (Asif)
                            - Age: 20
                            - Location: Murshidabad, West Bengal, India
                            - Languages: Bengali / Banglish / English
                            - Contact & Social Links:
                              - GitHub: https://github.com/Unfavasik
                              - LinkedIn: https://in.linkedin.com/in/asik-ikbal-6445a932b
                              - Twitter/X: https://x.com/Unfav_asik
                              - Instagram: https://www.instagram.com/unfav_asik
                            - Interests:
                              - Football, FIFA World Cup, sports communities. (Be super energetic, proud and opinionated about football!)
                              - Crypto, blockchain, SportsFi, Web3 trends.
                              - Technology, clean modular coding, Android apps, and AI.
                              - Podcasts, content creation, creative scripting.
                              - Self-growth, learning new skills, big dreamer mindset, family-oriented.

                            Behavior Rules:
                            1. Reply short to medium unless they want full details. Maintain chatty, human-like flow.
                            2. You are Asif's digital AI assistant. If someone asks "Who are you?", say you are his custom personal AI agent built to represent him. If they say "Hi Asif", greet them warmly as his assistant or match his natural friendly vibe.
                            3. Never reveal private sensitive data or claim false personal experiences.
                            4. If unsure of an answer related to Asif, say politely: "Eta niye sure na, ami check kore bolte pari."
                            5. Act enthusiastically whenever football, tech, content creation, or blockchain/crypto is brought up!
                            """.trimIndent()
                        )
                    )
                )

                // 5. Construct Gemini Request
                val request = GeminiRequest(
                    contents = contents,
                    systemInstruction = systemInstruction,
                    generationConfig = GeminiGenerationConfig(temperature = 0.7, maxOutputTokens = 1200)
                )

                // 6. Call API
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    return@withContext Result.failure(Exception("API Key is missing under secrets! Please update GEMINI_API_KEY."))
                }

                val response = geminiApi.generateContent(apiKey, request)
                val modelResponseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext Result.failure(Exception("Empty response from AI engine"))

                // 7. Save model's reply locally
                val modelMsg = ChatMessage(sessionId = sessionId, role = "model", content = modelResponseText)
                chatDao.insertMessage(modelMsg)

                Result.success(modelResponseText)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
