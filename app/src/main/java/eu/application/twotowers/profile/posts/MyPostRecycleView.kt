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
import eu.application.twotowers.R
import eu.application.twotowers.databinding.MyPostCardProfileBinding
import eu.application.twotowers.databinding.MyPostCardProfileBindingImpl

class MyPostRecycleView(private val context : Context): RecyclerView.Adapter<MyPostRecycleView.PostViewHolder>() {

    var postList : MutableList<PostInfo?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    object DeletedPost{
        var deletedPost : MutableLiveData<PostInfo?> = MutableLiveData()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostRecycleView.PostViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = MyPostCardProfileBindingImpl.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyPostRecycleView.PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bindPost(post , position)
        holder.bindImages(post?.houseImages)
    }

    inner class PostViewHolder (private val binding : MyPostCardProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val viewPager = binding.imageCarousel
        private val indicatorLayout = binding.constraintLayout
        private val postSection = binding.goneLayout
        private val layoutClickExpand = binding.constraintLayout3


        fun bindPost (post : PostInfo? , position: Int){
            binding.post = post
            layoutClickExpand.setOnClickListener {
                if(postSection.visibility == View.GONE)
                    postSection.visibility = View.VISIBLE
                else
                    postSection.visibility = View.GONE
            }
            binding.deletePost.setOnClickListener {
                areUSureDialog(post , position)
            }
            binding.executePendingBindings()
        }


        fun bindImages(image : List<Uri?>?){
            val adapter = image?.let { ImagePagerAdapter(itemView.context, it) }
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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



        private fun updateIndicators(selectedIndex : Int){
            for(i in 0 until indicatorLayout.childCount){
                val indicator = indicatorLayout.getChildAt(i)
                Log.d("Indicator", "Child view at index $i: ${indicator.javaClass.simpleName}")

                if(i == selectedIndex){
                    indicator.setBackgroundResource(R.drawable.circle_dark)
                    indicator.bringToFront()
                }
                else{
                    indicator.setBackgroundResource(R.drawable.circle_light)
                }
            }
        }

    }

    private fun areUSureDialog(post : PostInfo? , position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete this post?")
        builder.setMessage("Are you sure you want to DELETE this post?")
        builder.setPositiveButton("Yes"){_,_->
            DeletedPost.deletedPost.value = post
            removeItem(position)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun removeItem(position: Int){
        postList.removeAt(position)
        notifyItemRemoved(position)
    }
}
