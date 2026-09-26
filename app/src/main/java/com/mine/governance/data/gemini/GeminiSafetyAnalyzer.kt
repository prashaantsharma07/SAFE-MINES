package com.mine.governance.data.gemini

import com.mine.governance.BuildConfig
import com.mine.governance.domain.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SafetyAiAnalysis(
    val category: String,
    val severity: Severity,
    val dgmsRegulation: String,
    val recommendedAction: String,
    val isLiveGemini: Boolean
)

class GeminiSafetyAnalyzer(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {

    suspend fun analyzeObservation(observationText: String, sector: String): SafetyAiAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isNotBlank()) {
            try {
                val liveResult = callGeminiApi(observationText, sector, apiKey)
                if (liveResult != null) return@withContext liveResult
            } catch (e: Exception) {
                // Fall back to rule-based DGMS analysis on network timeout or quota
            }
        }

        // DGMS CMR-2017 Regulatory Rule Engine Fallback
        evaluateLocalDgmsRules(observationText, sector)
    }

    private fun callGeminiApi(observationText: String, sector: String, apiKey: String): SafetyAiAnalysis? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val prompt = """
            You are a Directorate General of Mines Safety (DGMS) certified Coal Mine Inspector.
            Analyze this field safety observation from $sector:
            "$observationText"

            Provide your response in raw JSON format with exact keys:
            {
              "category": "string (Ventilation & Gas / Strata & Roof Control / Mechanical & Haulage / Electrical / Occupational Safety)",
              "severity": "CRITICAL" or "HIGH" or "MEDIUM" or "LOW",
              "dgmsRegulation": "string (e.g. CMR 2017 Regulation 153/160)",
              "recommendedAction": "string (specific corrective steps)"
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseBody = response.body?.string() ?: return null

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            val cleanedJson = rawText.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanedJson)

            val cat = parsed.optString("category", "General Safety")
            val sevStr = parsed.optString("severity", "MEDIUM")
            val dgms = parsed.optString("dgmsRegulation", "CMR 2017 Rule 112")
            val action = parsed.optString("recommendedAction", "Conduct physical inspection and isolate hazard.")

            val severity = when (sevStr.uppercase()) {
                "CRITICAL" -> Severity.CRITICAL
                "HIGH" -> Severity.HIGH
                "MEDIUM" -> Severity.MEDIUM
                else -> Severity.LOW
            }

            return SafetyAiAnalysis(
                category = cat,
                severity = severity,
                dgmsRegulation = dgms,
                recommendedAction = action,
                isLiveGemini = true
            )
        }
    }

    private fun evaluateLocalDgmsRules(text: String, sector: String): SafetyAiAnalysis {
        val lower = text.lowercase()
        return when {
            lower.contains("gas") || lower.contains("methane") || lower.contains("ch4") || lower.contains("co") || lower.contains("air") -> {
                SafetyAiAnalysis(
                    category = "Ventilation & Mine Gases",
                    severity = if (lower.contains("leak") || lower.contains("high")) Severity.CRITICAL else Severity.HIGH,
                    dgmsRegulation = "CMR 2017 Reg. 153 (Ventilation & Inflammable Gas Standards)",
                    recommendedAction = "Immediately verify auxiliary fan flow and test with methanometer. Restrict unauthorized entry.",
                    isLiveGemini = false
                )
            }
            lower.contains("roof") || lower.contains("crack") || lower.contains("fall") || lower.contains("strata") || lower.contains("bolt") -> {
                SafetyAiAnalysis(
                    category = "Strata & Roof Control",
                    severity = Severity.CRITICAL,
                    dgmsRegulation = "CMR 2017 Reg. 123 (Systematic Support Rules & Strata Control)",
                    recommendedAction = "Deploy tell-tale extensometer. Set additional hydraulic props and timber cogs immediately.",
                    isLiveGemini = false
                )
            }
            lower.contains("belt") || lower.contains("conveyor") || lower.contains("machine") || lower.contains("pump") -> {
                SafetyAiAnalysis(
                    category = "Machinery & Haulage",
                    severity = Severity.HIGH,
                    dgmsRegulation = "CMR 2017 Reg. 92 (Conveyor Belt & Haulage Safety)",
                    recommendedAction = "Isolate power supply at circuit breaker. Check emergency pull-cord switch and bearings.",
                    isLiveGemini = false
                )
            }
            lower.contains("water") || lower.contains("dust") || lower.contains("slurry") -> {
                SafetyAiAnalysis(
                    category = "Environmental & Water Hazard",
                    severity = Severity.MEDIUM,
                    dgmsRegulation = "CMR 2017 Reg. 143 (Coal Dust Explosion Prevention)",
                    recommendedAction = "Activate high-pressure water spray barriers and stone-dusting misting nozzles.",
                    isLiveGemini = false
                )
            }
            else -> {
                SafetyAiAnalysis(
                    category = "General Occupational Compliance",
                    severity = Severity.LOW,
                    dgmsRegulation = "CMR 2017 Reg. 182 (General Safety & PPE Requirements)",
                    recommendedAction = "Log in shift supervisor register. Verify standard worker PPE compliance.",
                    isLiveGemini = false
                )
            }
        }
    }
}
