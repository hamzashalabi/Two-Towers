package eu.application.twotowers.chat

import android.net.Uri

interface FirebaseReteriver {
    fun retrieveUserData(callback : (List<User?>) -> Unit)
    fun sendConversation(message : Message , callBack : (success : Boolean)-> Unit)
    fun retrieveConversation(callback : (MutableList<Message?>) -> Unit)
    fun retrieveLatestMessage(callback : (MutableList<Message?>) -> Unit)
    fun retrieveCurrentUserImage(callback:(Uri?)-> Unit)
}