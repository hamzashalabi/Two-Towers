package eu.application.twotowers.profile.postapproval

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.application.twotowers.databinding.PostApprovalBinding
import eu.application.twotowers.databinding.PostApprovalBindingImpl

@RequiresApi(Build.VERSION_CODES.O)
class PostApprovalActivity : AppCompatActivity() {


    private lateinit var binding : PostApprovalBinding
    private lateinit var viewModel : PostApprovalViewModel
    private lateinit var postApprovalRecycleView: PostApprovalRecycleView

    private var uid : String? = null
    private var pid : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostApprovalBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebasePostApprovalManager = FirebasePostApprovalManager()
        val postApprovalViewModelFactory = PostApprovalViewModelFactory(firebasePostApprovalManager)
        viewModel = ViewModelProvider(this , postApprovalViewModelFactory)[PostApprovalViewModel::class.java]

        postApprovalRecycleView = PostApprovalRecycleView(this)
        binding.postApprovalRecycleView.adapter = postApprovalRecycleView
        binding.postApprovalRecycleView.layoutManager = LinearLayoutManager(this)


        binding.postApprovalButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (postApprovalRecycleView.postList.isEmpty()){
            PostApprovalRecycleView.Post.invokePostRetrieval.postValue(true)
        }

        PostApprovalRecycleView.Post.invokePostRetrieval.observe(this){fetchAllPosts->
            if(fetchAllPosts){
        viewModel.fetchAllPosts()
            }
        }

        viewModel.postList.observe(this){postList->
            postApprovalRecycleView.postList = postList.toMutableList()
            postApprovalRecycleView.notifyDataSetChanged()
        }


        PostApprovalRecycleView.Post.acceptedPost.observe(this){
            uid = it?.uid
            pid = it?.pid
            val approvalResult = ApprovalResult(uid , pid , "accepted")
            viewModel.approvedPost(approvalResult)
        }


        PostApprovalRecycleView.Post.rejectedPost.observe(this){
            uid = it?.uid
            pid = it?.pid
            val approvalResult = ApprovalResult(uid , pid , "" , false)
            viewModel.updateWarning(approvalResult)
        }

        PostApprovalRecycleView.Post.warningPost.observe(this){
            uid = it?.uid
            Log.e("activity uid","$uid")
            pid = it?.pid
            Log.e("activity pid","$pid")
            val approvalResult = ApprovalResult(uid , pid , "" ,true)
            viewModel.updateWarning(approvalResult)
        }
    }
}