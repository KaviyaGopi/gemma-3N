package com.example.gemmaai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import de.kherud.llama.LlamaModel
import de.kherud.llama.ModelParameters
import de.kherud.llama.InferenceParameters
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class GemmaModel(private val context: Context) {
    private var llamaModel: LlamaModel? = null
    private var isInitialized = false
    
    companion object {
        private const val TAG = "GemmaModel"
        private const val MODEL_FILENAME = "gemma-3n-E4B-it-F16.gguf"
        private const val MAX_TOKENS = 512
        private const val CONTEXT_SIZE = 2048
    }
    
    suspend fun initializeModel(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isInitialized && llamaModel != null) {
                Log.d(TAG, "Model already initialized")
                return@withContext true
            }
            
            Log.d(TAG, "Starting model initialization...")
            
            val modelFile = extractModelFromAssets()
            if (modelFile == null || !modelFile.exists()) {
                Log.e(TAG, "Model file not found or extraction failed")
                return@withContext false
            }
            
            Log.d(TAG, "Model file ready: ${modelFile.absolutePath}")
            
            val modelParams = ModelParameters().apply {
                modelFilePath = modelFile.absolutePath
                nGpuLayers = 0 // CPU only for mobile
                nThreads = minOf(Runtime.getRuntime().availableProcessors(), 8)
                nCtx = CONTEXT_SIZE
                verbose = false
            }
            
            llamaModel = LlamaModel(modelParams)
            isInitialized = true
            
            Log.d(TAG, "Model initialized successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize model", e)
            false
        }
    }
    
    private suspend fun extractModelFromAssets(): File? = withContext(Dispatchers.IO) {
        try {
            val modelDir = File(context.filesDir, "models")
            if (!modelDir.exists()) {
                modelDir.mkdirs()
            }
            
            val modelFile = File(modelDir, MODEL_FILENAME)
            
            // Check if model already exists and has correct size
            if (modelFile.exists() && modelFile.length() > 0) {
                Log.d(TAG, "Model file already exists: ${modelFile.length()} bytes")
                return@withContext modelFile
            }
            
            Log.d(TAG, "Extracting model from assets...")
            
            // Extract from assets
            val inputStream: InputStream = context.assets.open(MODEL_FILENAME)
            val outputStream = FileOutputStream(modelFile)
            
            val buffer = ByteArray(8192)
            var totalBytes = 0L
            var bytesRead: Int
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytes += bytesRead
                
                // Log progress every 100MB
                if (totalBytes % (100 * 1024 * 1024) == 0L) {
                    Log.d(TAG, "Extracted ${totalBytes / (1024 * 1024)}MB")
                }
            }
            
            inputStream.close()
            outputStream.close()
            
            Log.d(TAG, "Model extraction completed: ${totalBytes / (1024 * 1024)}MB")
            modelFile
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract model from assets", e)
            null
        }
    }
    
    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized || llamaModel == null) {
                return@withContext "Model not initialized. Please wait for initialization to complete."
            }
            
            val formattedPrompt = formatPrompt(prompt)
            Log.d(TAG, "Generating response for prompt length: ${formattedPrompt.length}")
            
            val inferenceParams = InferenceParameters().apply {
                temperature = 0.7f
                topK = 40
                topP = 0.9f
                repeatPenalty = 1.1f
                nPredict = MAX_TOKENS
            }
            
            val response = llamaModel!!.complete(formattedPrompt, inferenceParams)
            val cleanedResponse = cleanResponse(response)
            
            Log.d(TAG, "Generated response length: ${cleanedResponse.length}")
            cleanedResponse
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating response", e)
            "Sorry, I encountered an error generating a response. Please try again."
        }
    }
    
    private fun formatPrompt(userInput: String): String {
        return """<bos><start_of_turn>user
$userInput<end_of_turn>
<start_of_turn>model
"""
    }
    
    private fun cleanResponse(response: String): String {
        return response
            .substringAfter("<start_of_turn>model")
            .substringBefore("<end_of_turn>")
            .substringBefore("<eos>")
            .trim()
            .takeIf { it.isNotEmpty() } ?: "I'm having trouble generating a response right now."
    }
    
    fun getModelStatus(): String {
        return when {
            isInitialized && llamaModel != null -> "Ready"
            isInitialized && llamaModel == null -> "Error"
            else -> "Initializing"
        }
    }
    
    fun cleanup() {
        try {
            llamaModel?.close()
            llamaModel = null
            isInitialized = false
            Log.d(TAG, "Model cleanup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }
}