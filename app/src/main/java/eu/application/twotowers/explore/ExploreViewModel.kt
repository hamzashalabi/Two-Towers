package eu.application.twotowers.explore


import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.application.twotowers.R

class ExploreViewModel(private val firebaseReteriver: FirebaseReteriver): ViewModel() {

    private val _postInfo = MutableLiveData<List<PostInfo?>>()
    val postInfo : LiveData<List<PostInfo?>> = _postInfo

    private val _likeStatus = MutableLiveData<LikeStatus>()
    val likeStatus :LiveData<LikeStatus> = _likeStatus

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePostInfo(){
        firebaseReteriver.retrievePostInfo {post->
            _postInfo.postValue(post)
        }
    }

    fun showMultiSelectDialog(optionsArray : Array<String>, selectedOptions : ArrayList<Int>,
                                      textView: TextView, context : Context, callback: MultiSelectDialogCallback){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Options")

        val drawable = ContextCompat.getDrawable(context , R.drawable.check)

        val checkedItem = BooleanArray(optionsArray.size){selectedOptions.contains(it)}

        builder.setMultiChoiceItems(optionsArray ,checkedItem){_, which, isChecked ->
            if(isChecked){
                selectedOptions.add(which)
            }else{
                selectedOptions.remove(which)
            }
        }

        builder.setPositiveButton("Ok"){ dialog, _ ->
            val selectedItemsText = selectedOptions.map { optionsArray[it] }
            dialog.dismiss()
            textView.text = "Checked"
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable , null , null , null)
            callback.onOptionsSelected(selectedItemsText)

        }

        builder.setNegativeButton("Cancel"){dialog, _ ->
            dialog.dismiss()
            callback.onOptionsSelected(emptyList())
        }

        builder.show()
    }

    fun likedPost(reference: LikeReference){
        firebaseReteriver.likedPost(reference){ success ->
            _likeStatus.value = if(success) LikeStatus.SUCCESS else LikeStatus.FAILURE
        }
    }

    fun retrieveCurrentUserImage(callback : (Uri?) -> Unit){
        firebaseReteriver.retrieveCurrentUserImage {userImage->
            callback(userImage)
        }
    }
}
