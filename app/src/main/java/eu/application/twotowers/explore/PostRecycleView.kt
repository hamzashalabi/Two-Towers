package eu.application.twotowers.explore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.application.twotowers.chat.ChatActivity
import eu.application.twotowers.databinding.PostCardBinding
import eu.application.twotowers.databinding.PostCardBindingImpl

class PostRecycleView (private val context : Context): RecyclerView.Adapter<PostRecycleView.PostViewHolder>() {

    var postList = mutableListOf<PostInfo?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    object LikedPost{
        var likedPost : MutableLiveData<PostInfo?> = MutableLiveData()
        var invokePostRetrieval =  MutableLiveData<Boolean>()
    }

    companion object{
        const val USER_IMAGE_POST = "image_key"
        const val USER_NAME_POST = "user_name"
        const val USER_TO_ID = "uid"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = PostCardBindingImpl.inflate(inflater , parent , false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bindPost(post)

        holder.bindImages(post?.houseImages)

        if (position == postList.size-2){
            LikedPost.invokePostRetrieval.postValue(true)
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun searchUser(searchList : List<PostInfo?>){
        postList = searchList.toMutableList()
        notifyDataSetChanged()
    }

    fun filterPosts(filteredList : List<PostInfo?>){
        postList = filteredList.toMutableList()
        notifyDataSetChanged()
    }


    inner class PostViewHolder(private val binding : PostCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

            private val viewPager = binding.imageCarousel
            private val postSection = binding.goneLayout
            private val layoutClickExpand = binding.constraintLayout3
            private val layoutClickMoveToChat = binding.constraintLayout2


            fun bindPost (post : PostInfo?){
                binding.post = post
                layoutClickExpand.setOnClickListener {
                    if(postSection.visibility == View.GONE)
                    postSection.visibility = View.VISIBLE
                    else
                        postSection.visibility = View.GONE
                }

                Glide.with(context).load(post?.userImage).into(binding.profileImageHolder)

                layoutClickMoveToChat.setOnClickListener {
                    val intent = Intent(itemView.context, ChatActivity::class.java)
                    intent.putExtra(USER_NAME_POST , post?.userName)
                    intent.putExtra(USER_IMAGE_POST, post?.userImage.toString())
                    intent.putExtra(USER_TO_ID , post?.uid)
                    itemView.context.startActivity(intent)
                }


                binding.fave.setOnClickListener {
                    binding.heart.visibility = View.VISIBLE
                    LikedPost.likedPost.value = post
                }

                binding.executePendingBindings()
            }


            fun bindImages(image : List<Uri?>?){
                if(image != null){
                val adapter = ImagePagerAdapter(context , image)
                viewPager.adapter = adapter
                }
            }

    }
}