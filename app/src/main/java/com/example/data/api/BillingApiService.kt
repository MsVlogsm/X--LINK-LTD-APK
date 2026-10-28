package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

data class NetworkResult(
    val statusCode: Int,
    val isSuccess: Boolean,
    val responseBody: String,
    val roundTripTimeMs: Long,
    val errorMessage: String? = null
)

class BillingApiClient {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun ping(baseUrl: String, apiKey: String, bearerToken: String): NetworkResult =
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val url = cleanUrl(baseUrl) + "ping"
            try {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("X-API-KEY", apiKey)
                    .addHeader("Authorization", bearerToken)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val elapsed = System.currentTimeMillis() - startTime
                    val body = response.body?.string() ?: ""
                    NetworkResult(
                        statusCode = response.code,
                        isSuccess = response.isSuccessful,
                        responseBody = body,
                        roundTripTimeMs = elapsed
                    )
                }
            } catch (e: Exception) {
                val elapsed = System.currentTimeMillis() - startTime
                NetworkResult(
                    statusCode = 0,
                    isSuccess = false,
                    responseBody = "",
                    roundTripTimeMs = elapsed,
                    errorMessage = e.localizedMessage ?: "Connection failed"
                )
            }
        }

    suspend fun postPaymentSync(
        baseUrl: String,
        apiKey: String,
        bearerToken: String,
        payloadJson: String
    ): NetworkResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val url = cleanUrl(baseUrl) + "payments/sync"
        try {
            val requestBody = payloadJson.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", apiKey)
                .addHeader("Authorization", bearerToken)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                val body = response.body?.string() ?: ""
                NetworkResult(
                    statusCode = response.code,
                    isSuccess = response.isSuccessful,
                    responseBody = body,
                    roundTripTimeMs = elapsed
                )
            }
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            NetworkResult(
                statusCode = 0,
                isSuccess = false,
                responseBody = "",
                roundTripTimeMs = elapsed,
                errorMessage = e.localizedMessage ?: "Sync request timed out"
            )
        }
    }

    private fun cleanUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }
}
