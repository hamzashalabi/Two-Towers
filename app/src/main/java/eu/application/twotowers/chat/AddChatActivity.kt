package eu.application.twotowers.chat

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.databinding.AddChatBinding
import eu.application.twotowers.databinding.AddChatBindingImpl

class AddChatActivity :AppCompatActivity(){

    private lateinit var binding : AddChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var addChatRecycleView : AddChatRecycleView
    private lateinit var newUserList : List<User?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddChatBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseDatabaseReteriver = FirebaseDatabaseReteriver()
        val viewModelFactory = ChatViewModelFactory(firebaseDatabaseReteriver)
        viewModel = ViewModelProvider(this , viewModelFactory)[ChatViewModel::class.java]
        binding.addChatViewModel = viewModel

        addChatRecycleView = AddChatRecycleView(this)
        binding.addChatRecycleViewLayout.adapter = addChatRecycleView
        binding.addChatRecycleViewLayout.layoutManager = LinearLayoutManager(this)

        newUserList = mutableListOf()
        viewModel.updateUserInfo { users->
            val usersList = users.filter {user->
                user?.uId != FirebaseAuth.getInstance().currentUser?.uid
            }
            addChatRecycleView.userList = usersList
            newUserList = usersList
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher
        }

        binding.searchPar.clearFocus()

        binding.searchPar.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchList(newText , addChatRecycleView , newUserList)
                return true
            }

        })

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }


}