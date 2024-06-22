package eu.application.twotowers.profile.postapproval

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.application.twotowers.databinding.ApprovalPostCardProfileBinding
import eu.application.twotowers.databinding.ApprovalPostCardProfileBindingImpl
import eu.application.twotowers.explore.ImagePagerAdapter

class PostApprovalRecycleView (private val context : Context): RecyclerView.Adapter<PostApprovalRecycleView.ApprovalViewHolder>()  {

    var postList : MutableList<PostInfo?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    object Post{
    var acceptedPost = MutableLiveData<PostInfo?>()
    var rejectedPost = MutableLiveData<PostInfo?>()
    var warningPost = MutableLiveData<PostInfo?>()
    var invokePostRetrieval =  MutableLiveData<Boolean>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostApprovalRecycleView.ApprovalViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ApprovalPostCardProfileBindingImpl.inflate(inflater , parent , false)
        return ApprovalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostApprovalRecycleView.ApprovalViewHolder, position: Int) {
        val post = postList[position]
        holder.bindPost(post)
        post?.houseImages?.let { holder.bindPostImages(it) }

        if (position == postList.size-2){
            Post.invokePostRetrieval.postValue(true)
        }

        holder.bindAccept(post , position)
        holder.bindReject(post , position)
        holder.bindWarning(post , position)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ApprovalViewHolder(private val binding : ApprovalPostCardProfileBinding) :
        RecyclerView.ViewHolder(binding.root){

        private val viewPager = binding.imageCarousel

            fun bindPost(postInfo: PostInfo?){
                binding.post = postInfo
                Glide.with(context).load(postInfo?.userImage).into(binding.profileImageHolder)
                binding.executePendingBindings()
            }

            fun bindPostImages(houseImages : List<Uri?>){
                val adapter = ImagePagerAdapter(itemView.context, houseImages)
                viewPager.adapter = adapter
            }

        fun bindAccept(postInfo: PostInfo? , position: Int){
            binding.acceptButton.setOnClickListener {
                Post.acceptedPost.value = postInfo
                removeItem(position)
            }
        }

        fun bindReject(postInfo: PostInfo? , position: Int){
            binding.rejectButton.setOnClickListener {
                Post.rejectedPost.value = postInfo
                removeItem(position)
            }
        }

        fun bindWarning(postInfo: PostInfo? , position: Int){
            binding.warningButton.setOnClickListener {
                Post.warningPost.value = postInfo
                removeItem(position)
            }
        }

        private fun removeItem(position: Int){
            postList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, postList.size)
        }

    }
}