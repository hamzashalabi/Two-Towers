package eu.application.twotowers.profile.posts

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import eu.application.twotowers.R
import eu.application.twotowers.databinding.SavedPostCardProfileBinding
import eu.application.twotowers.databinding.SavedPostCardProfileBindingImpl

class SavedPostsRecycleView (private val context : Context): RecyclerView.Adapter<SavedPostsRecycleView.PostViewHolder>() {

    var likedPostList : MutableList<LikedPostInfo?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    object UnlikedPost{
        var unlikedPost : MutableLiveData<LikedPostInfo?> = MutableLiveData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SavedPostCardProfileBindingImpl.inflate(inflater , parent , false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return likedPostList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = likedPostList[position]
        holder.bindPost(post , position)
        holder.bindImages(post?.houseImages)
    }

    inner class PostViewHolder (private val binding : SavedPostCardProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val viewPager = binding.imageCarousel
        private val indicatorLayout = binding.constraintLayout
        private val postSection = binding.goneLayout
        private val layoutClickExpand = binding.constraintLayout3

        fun bindPost(post: LikedPostInfo? , position : Int) {
            binding.post = post
            layoutClickExpand.setOnClickListener {
                if (postSection.visibility == View.GONE)
                    postSection.visibility = View.VISIBLE
                else
                    postSection.visibility = View.GONE
            }

            Glide.with(context).load(post?.userImage).into(binding.profileImageHolder)

            binding.unlikePost.setOnClickListener {
                areUSureDialog(post , position)
            }
            binding.executePendingBindings()
        }

        fun bindImages(images: List<Uri?>?) {
            val adapter = images?.let { ImagePagerAdapter(itemView.context, it) }
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // not needed for our code
                }

                override fun onPageSelected(position: Int) {
                    updateIndicators(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    //not needed for our code
                }

            })
        }


        private fun updateIndicators(selectedIndex: Int) {
            for (i in 0 until indicatorLayout.childCount) {
                val indicator = indicatorLayout.getChildAt(i)
                Log.d("Indicator", "Child view at index $i: ${indicator.javaClass.simpleName}")

                if (i == selectedIndex) {
                    indicator.setBackgroundResource(R.drawable.circle_dark)
                    indicator.bringToFront()
                } else {
                    indicator.setBackgroundResource(R.drawable.circle_light)
                }
            }
        }
    }

    private fun areUSureDialog(post : LikedPostInfo? , position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Remove this post?")
        builder.setMessage("Are you sure you want to REMOVE this post?")
        builder.setPositiveButton("Yes"){_,_->
            UnlikedPost.unlikedPost.value = post
            removeItem(position)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun removeItem(position: Int){
        likedPostList.removeAt(position)
        notifyItemRemoved(position)
    }

}