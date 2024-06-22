package eu.application.twotowers.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ChatViewModel(private val firebaseReteriver: FirebaseReteriver) : ViewModel() {

    private val usersInfo = MutableLiveData<List<User?>>()

    private var messageList = MutableLiveData<List<Message?>>()


    private val _messageStatus = MutableLiveData<MessageStatus>()
    val messageStatus : LiveData<MessageStatus> = _messageStatus

    fun updateUserInfo(callback : (List<User?>) -> Unit){
        firebaseReteriver.retrieveUserData {users ->
            usersInfo.postValue(users)
            callback(users)
        }
    }

    fun searchList(name : String , addChatRecycleView: AddChatRecycleView , newUserList : List<User?>){
        val searchList = mutableListOf<User?>()
        if (name.isBlank()){
            addChatRecycleView.searchUser(newUserList)
        }
        for(user in addChatRecycleView.userList){
            if (user?.userName?.lowercase()?.contains(name.lowercase())==true){
                searchList.add(user)
            }
        }
        addChatRecycleView.searchUser(searchList)
    }

    fun saveUserMessages(message: Message){
        firebaseReteriver.sendConversation(message){success ->
            _messageStatus.value = if(success) MessageStatus.SUCCESS else MessageStatus.FAILURE
        }
    }

    fun retrieveUserMessages(callback: (MutableList<Message?>) -> Unit){
        firebaseReteriver.retrieveConversation { messages ->
            messageList.postValue(messages)
            callback(messages)
        }
    }

    fun retrieveLatestMessage(callback: (MutableList<Message?>) -> Unit){
        firebaseReteriver.retrieveLatestMessage { messages ->
            Log.e("view model","view model something")
            callback(messages)
        }
    }

    fun updateUserInfoAndMessages(callback: (List<User?>, List<Message?>) -> Unit) {
        firebaseReteriver.retrieveUserData { users ->
            firebaseReteriver.retrieveLatestMessage { messages ->
                val usersWithMessages = mutableListOf<User?>()
                users.forEach { user ->
                    if (user?.uId != FirebaseAuth.getInstance().currentUser?.uid) {
                        val hasMessages = messages.any { message ->
                            message?.fromId == user?.uId || message?.toId == user?.uId
                        }
                        if (hasMessages) {
                            usersWithMessages.add(user)
                        }
                    }
                }
                callback(usersWithMessages, messages)
            }
        }
    }

    fun retrieveCurrentUserImage(callback : (Uri?) -> Unit){
        firebaseReteriver.retrieveCurrentUserImage {userImage->
            callback(userImage)
        }
    }

}