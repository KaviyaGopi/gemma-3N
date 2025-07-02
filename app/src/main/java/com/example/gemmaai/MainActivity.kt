package com.example.gemmaai

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gemmaai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: GemmaViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupUI()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }
    }
    
    private fun setupUI() {
        binding.apply {
            buttonSend.setOnClickListener {
                sendMessage()
            }
            
            editTextMessage.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage()
                    true
                } else {
                    false
                }
            }
            
            editTextMessage.addTextChangedListener { text ->
                buttonSend.isEnabled = !text.isNullOrBlank() && 
                    viewModel.isModelReady.value == true &&
                    viewModel.isLoading.value != true
            }
            
            buttonClear.setOnClickListener {
                viewModel.clearConversation()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.conversationHistory.observe(this) { messages ->
            chatAdapter.updateMessages(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerViewChat.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.apply {
                progressBar.visibility = if (isLoading) 
                    android.view.View.VISIBLE 
                else 
                    android.view.View.GONE
                    
                buttonSend.isEnabled = !isLoading && 
                    !editTextMessage.text.isNullOrBlank() &&
                    viewModel.isModelReady.value == true
            }
        }
        
        viewModel.isModelReady.observe(this) { isReady ->
            binding.apply {
                editTextMessage.isEnabled = isReady
                buttonSend.isEnabled = isReady && !editTextMessage.text.isNullOrBlank()
                
                if (isReady) {
                    editTextMessage.hint = "Type your message..."
                } else {
                    editTextMessage.hint = "Loading model..."
                }
            }
        }
        
        viewModel.initializationProgress.observe(this) { progress ->
            binding.textViewStatus.text = progress
        }
    }
    
    private fun sendMessage() {
        val message = binding.editTextMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            binding.editTextMessage.text?.clear()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearConversation()
    }
}