package eu.application.twotowers.profile.posts

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.application.twotowers.databinding.MyPostBinding
import eu.application.twotowers.databinding.MyPostBindingImpl


class MyPostActivity : AppCompatActivity() {

    private lateinit var binding: MyPostBinding
    private lateinit var viewModel: PostViewModel
    private lateinit var myPostRecycleView : MyPostRecycleView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPostBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebasePostRetrieverManager = FirebasePostRetrieverManager()
        val postViewModelFactory = PostViewModelFactory(firebasePostRetrieverManager)
        viewModel = ViewModelProvider(this, postViewModelFactory)[PostViewModel::class.java]

        myPostRecycleView = MyPostRecycleView(this)
        binding.myPostRecycleView.adapter = myPostRecycleView
        binding.myPostRecycleView.layoutManager = LinearLayoutManager(this)

        viewModel.myPosts { myPosts ->
            myPostRecycleView.postList = myPosts.toMutableList()
        }

        binding.myPostsButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        MyPostRecycleView.DeletedPost.deletedPost.observe(this){
            if(it != null){
                viewModel.deletePost(it.pid.toString())
                Toast.makeText(this, "Post Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }
}