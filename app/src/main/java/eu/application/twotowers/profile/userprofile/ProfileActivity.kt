package eu.application.twotowers.profile.userprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import eu.application.twotowers.chat.ChatListActivity
import eu.application.twotowers.create.CreatePostActivity
import eu.application.twotowers.databinding.ProfileBinding
import eu.application.twotowers.databinding.ProfileBindingImpl
import eu.application.twotowers.explore.ExploreActivity
import eu.application.twotowers.map.MapActivity
import eu.application.twotowers.profile.account.AccountActivity
import eu.application.twotowers.profile.feedback.ReceiveFeedbackActivity
import eu.application.twotowers.profile.feedback.SendFeedbackActivity
import eu.application.twotowers.profile.postapproval.PostApprovalActivity
import eu.application.twotowers.profile.posts.MyPostActivity
import eu.application.twotowers.profile.posts.SavedPostsActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private var warningCount : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val userProfileRetrieverManager = UserProfileRetrieverManager()
        val profileViewModelFactory = ProfileViewModelFactory(userProfileRetrieverManager)
        viewModel = ViewModelProvider(this, profileViewModelFactory)[ProfileViewModel::class.java]

        binding.userProfile = viewModel


        viewModel.fetchUserProfile {user->
            binding.userNameTextView.text = user?.userName
            adminAccess(user!!.userRole)
            Glide.with(this).load(user.userImage).into(binding.userImageView)
            Glide.with(this).load(user.userImage).into(binding.ProfilePic)

            warningCount = user.warning
            if (warningCount!! > 0 ){
                binding.notification.visibility = View.VISIBLE
            }
        }

        binding.notification.setOnClickListener {
            warningAlert(this , warningCount!!)
        }


        binding.editProfileButton.setOnClickListener {
        viewModel.nav(this , AccountActivity::class.java)
        }

        binding.feedbackButton.setOnClickListener {
        viewModel.nav(this , ReceiveFeedbackActivity::class.java)
        }

        binding.sendFeedbackButton.setOnClickListener {
            val intent = Intent(this, SendFeedbackActivity::class.java)
            intent.putExtra("userName" , binding.userNameTextView.text)
            startActivity(intent)
        }

        binding.postApprovalButton.setOnClickListener {
        viewModel.nav(this , PostApprovalActivity::class.java)
        }

        binding.myPostsButton.setOnClickListener {
        viewModel.nav(this , MyPostActivity::class.java)
        }

        binding.savedPostsButton.setOnClickListener {
        viewModel.nav(this , SavedPostsActivity::class.java)
        }

        binding.MapAfter.setOnClickListener {
            viewModel.nav(this , MapActivity::class.java)
        }

        binding.ExploreBefore.setOnClickListener {
            viewModel.nav(this , ExploreActivity::class.java)
        }

        binding.CreateBefore.setOnClickListener {
            viewModel.nav(this , CreatePostActivity::class.java)
        }

        binding.ChatBefore.setOnClickListener {
            viewModel.nav(this , ChatListActivity::class.java)
        }

    }

    private fun adminAccess(role : String?){
        if(role == "admin"){
            binding.postApprovalButton.visibility = View.VISIBLE
            binding.postApprovalIcon.visibility = View.VISIBLE
            binding.postApprovalText.visibility = View.VISIBLE
            binding.postApprovalRightIcon.visibility = View.VISIBLE

            binding.feedbackButton.visibility = View.VISIBLE
            binding.feedbackIcon.visibility = View.VISIBLE
            binding.feedbackText.visibility = View.VISIBLE
            binding.feedbackRightIcon.visibility = View.VISIBLE

            binding.adminAccessIcon.visibility = View.VISIBLE
            binding.divider2.visibility = View.VISIBLE
        }
    }

    private fun warningAlert(context : Context , warningCount : Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning!!")
        builder.setMessage("Your latest post has violated our community guidelines. Please refrain from adding irrelevant content. You currently have $warningCount warnings. " +
                "If the warning count exceeds 2, your account will be deleted permanently.")
        builder.setPositiveButton("ok"){dialog,_->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}