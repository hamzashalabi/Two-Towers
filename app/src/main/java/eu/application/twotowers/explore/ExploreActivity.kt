package eu.application.twotowers.explore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import eu.application.twotowers.R
import eu.application.twotowers.chat.ChatListActivity
import eu.application.twotowers.create.CreatePostActivity
import eu.application.twotowers.databinding.ExploreBinding
import eu.application.twotowers.databinding.ExploreBindingImpl
import eu.application.twotowers.map.MapActivity
import eu.application.twotowers.profile.userprofile.ProfileActivity


class ExploreActivity :AppCompatActivity(){

    private lateinit var binding : ExploreBinding
    private lateinit var viewModel : ExploreViewModel
    private lateinit var postRecycleView :PostRecycleView
    private lateinit var newPostList : List<PostInfo?>
    private var isFeed = MutableLiveData<Boolean>()


    private lateinit var textViewCity : TextView
    private lateinit var textViewProperty: TextView
    private lateinit var textViewRooms: TextView
    private lateinit var textViewBathrooms: TextView


    private val cityOptions = ArrayList<Int>()
    private val propertyOptions = ArrayList<Int>()
    private val roomsOptions = ArrayList<Int>()
    private val bathroomsOptions = ArrayList<Int>()

    private var filteredCity = mutableListOf<String>()
    private var filteredProperty = mutableListOf<String>()
    private var filteredRooms = mutableListOf<String>()
    private var filteredBathrooms = mutableListOf<String>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExploreBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseReteriverManager = FirebaseReteriverManager()
        val viewModelFactory = ExploreViewModelFactory(firebaseReteriverManager)
        viewModel = ViewModelProvider(this, viewModelFactory)[ExploreViewModel::class.java]
        binding.viewModel=viewModel


        newPostList = mutableListOf()
        postRecycleView = PostRecycleView(this)
        binding.recycleView.adapter =postRecycleView
        binding.recycleView.layoutManager = LinearLayoutManager(this)

        if (postRecycleView.postList.isEmpty()){
            viewModel.updatePostInfo()
        }

        PostRecycleView.LikedPost.invokePostRetrieval.observe(this){ fetchAllPosts->
            if(fetchAllPosts){
                viewModel.updatePostInfo()
            }
        }

        viewModel.postInfo.observe(this){postList->
            postRecycleView.postList = postList.toMutableList()
            newPostList = postList
            isFeed.postValue(true)
            postRecycleView.notifyDataSetChanged()
        }

        viewModel.likeStatus.observe(this){status->
            when (status){
                LikeStatus.SUCCESS->{
                    Log.e("Like activity ","success")
                }
                LikeStatus.FAILURE->{
                    Log.e("Like activity ","failure")
                }
            }
        }

        PostRecycleView.LikedPost.likedPost.observe(this){
            if (it != null) {
                val uid = it.uid
                val pid = it.pid
                val reference = LikeReference(uid , pid)
                viewModel.likedPost(reference)
            }
        }

        filteredCity.addAll(resources.getStringArray(R.array.governorate))
        filteredProperty.addAll(resources.getStringArray(R.array.Property_Options_Cat))
        filteredRooms.addAll(resources.getStringArray(R.array.Rooms_Cat))
        filteredBathrooms.addAll(resources.getStringArray(R.array.Rooms_Cat))

        val searchView =binding.searchPar
        searchView.clearFocus()
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }

        })

        binding.MapAfter.setOnClickListener {
            nav(this , MapActivity::class.java)
        }

        binding.CreateBefore.setOnClickListener {
            nav(this , CreatePostActivity::class.java)
        }

        binding.ChatBefore.setOnClickListener {
            nav(this , ChatListActivity::class.java)
        }

        binding.filterPar.setOnClickListener {
            showDialog(this)
        }

        binding.ProfilePic.setOnClickListener {
            nav(this , ProfileActivity::class.java)
        }

        binding.removeFilter.setOnClickListener {

            filteredCity.clear()
            filteredProperty.clear()
            filteredRooms.clear()
            filteredBathrooms.clear()

            filteredCity.addAll(resources.getStringArray(R.array.governorate))
            filteredProperty.addAll(resources.getStringArray(R.array.Property_Options_Cat))
            filteredRooms.addAll(resources.getStringArray(R.array.Rooms_Cat))
            filteredBathrooms.addAll(resources.getStringArray(R.array.Rooms_Cat))

            postRecycleView.filterPosts(newPostList)

            binding.removeFilter.visibility = View.INVISIBLE
        }

        val pId = intent.getStringExtra(MapActivity.USER_NAME_KEY)
        if(pId != null){
            pIdSearchList(pId.toString())
        }

        viewModel.retrieveCurrentUserImage { userImage->
            Glide.with(this).load(userImage).into(binding.ProfilePic)
        }

    }
    fun searchList(name : String){
        val searchList = mutableListOf<PostInfo?>()
        if (name.isBlank()){
            postRecycleView.searchUser(newPostList)
        }
        for(post in postRecycleView.postList){
            if (post?.userName?.lowercase()?.contains(name.lowercase())==true){
                searchList.add(post)
            }
        }
        postRecycleView.searchUser(searchList)
    }


    private fun pIdSearchList(pId : String){
        val searchList = mutableListOf<PostInfo?>()
        var isFound = true

        isFeed.observe(this){
        postRecycleView.postList.forEach { post->
            if (isFound){
            if (pId == post?.pid){
                searchList.add(post)
                isFound = false
            }
        }
        }
            postRecycleView.searchUser(searchList)
    }
    }

    private fun showDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Categories")

        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.alert_dialog, null)
        textViewCity = customView.findViewById(R.id.city)
        textViewProperty = customView.findViewById(R.id.property)
        textViewRooms = customView.findViewById(R.id.rooms)
        textViewBathrooms = customView.findViewById(R.id.bathrooms)

        textViewCity.setOnClickListener {
            reduce(textViewCity, resources.getStringArray(R.array.governorate), cityOptions , filteredCity)
        }

        textViewProperty.setOnClickListener {
            reduce(textViewProperty, resources.getStringArray(R.array.Property_Options_Cat), propertyOptions , filteredProperty)
        }

        textViewRooms.setOnClickListener {
            reduce(textViewRooms, resources.getStringArray(R.array.Rooms_Cat), roomsOptions , filteredRooms)
        }

        textViewBathrooms.setOnClickListener {
            reduce(textViewBathrooms, resources.getStringArray(R.array.Rooms_Cat), bathroomsOptions , filteredBathrooms)
        }

        builder.setPositiveButton("Ok"){ dialog, _ ->
            val filteredPost = postRecycleView.postList.filter { post->
                filteredCity.any{it == post?.city}&&
                filteredProperty.any{it == post?.propertyOptions}&&
                filteredRooms.any{it == post?.numRooms}&&
                filteredBathrooms.any{it == post?.numBathrooms}
            }

            postRecycleView.filterPosts(filteredPost)


            binding.removeFilter.visibility = View.VISIBLE


            dialog.dismiss()

        }

        builder.setNegativeButton("Cancel"){dialog, _ ->
            dialog.dismiss()

        }

        builder.setView(customView)
        val dialog = builder.create()
        dialog.show()
    }

    private fun reduce(textView: TextView, resourceArray : Array<String>, options : ArrayList<Int> , filteredResult : MutableList<String>){
            viewModel.showMultiSelectDialog(resourceArray , options ,
                textView ,this , object : MultiSelectDialogCallback{
                    override fun onOptionsSelected(selectedItems: List<String>?) {
                        if (!selectedItems.isNullOrEmpty()) {
                            filteredResult.clear()
                            filteredResult.addAll(selectedItems)
                        } else {
                            filteredResult.addAll(resourceArray)
                        }
                        options.clear()
                }
        })
    }

    private fun nav(from : Activity, to : Class<out Activity>){
        val intent = Intent(from , to)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        from.startActivity(intent)
    }
}



