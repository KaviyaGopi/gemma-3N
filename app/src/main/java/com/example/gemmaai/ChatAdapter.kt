package com.example.gemmaai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {
    
    private var messages = listOf<ChatMessage>()
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_USER -> R.layout.item_chat_user
            VIEW_TYPE_ASSISTANT -> R.layout.item_chat_assistant
            else -> R.layout.item_chat_system
        }
        
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }
    
    override fun getItemCount(): Int = messages.size
    
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isSystem -> VIEW_TYPE_SYSTEM
            message.isUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_ASSISTANT
        }
    }
    
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        
        fun bind(message: ChatMessage) {
            textMessage.text = message.content
            textTime.text = dateFormat.format(Date(message.timestamp))
        }
    }
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
        private const val VIEW_TYPE_SYSTEM = 3
    }
}