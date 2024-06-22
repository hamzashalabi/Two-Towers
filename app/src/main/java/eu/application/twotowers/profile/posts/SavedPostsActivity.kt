package eu.application.twotowers.profile.posts

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.application.twotowers.databinding.SavedPostsBinding
import eu.application.twotowers.databinding.SavedPostsBindingImpl

class SavedPostsActivity :AppCompatActivity() {

    private lateinit var binding: SavedPostsBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var savedPostsRecycleView: SavedPostsRecycleView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SavedPostsBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebasePostRetrieverManager = FirebasePostRetrieverManager()
        val postViewModelFactory = PostViewModelFactory(firebasePostRetrieverManager)
        viewModel = ViewModelProvider(this, postViewModelFactory)[PostViewModel::class.java]

        savedPostsRecycleView = SavedPostsRecycleView(this)
        binding.savedPostsRecycleView.adapter = savedPostsRecycleView
        binding.savedPostsRecycleView.layoutManager = LinearLayoutManager(this)

        viewModel.savedPosts {postList->
            savedPostsRecycleView.likedPostList = postList.toMutableList()
        }
        binding.savedPostsButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        SavedPostsRecycleView.UnlikedPost.unlikedPost.observe(this){
            if(it != null){
                viewModel.unlikePost(it.key!!)
            }
        }
    }
}
