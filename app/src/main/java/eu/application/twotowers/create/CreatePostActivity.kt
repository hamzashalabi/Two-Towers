package eu.application.twotowers.create

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.R
import eu.application.twotowers.databinding.CreatePostBinding
import eu.application.twotowers.databinding.CreatePostBindingImpl
import eu.application.twotowers.map.MapActivity
import java.lang.Integer.min

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: CreatePostBinding
    private lateinit var viewModel: CreatePostViewModel
    private lateinit var imageAccessPermissionHelper: ImageAccessPermissionHelper
    private var imageUris : MutableList<Uri> = mutableListOf()

    override fun onResume() {
        super.onResume()

        val governorates = resources.getStringArray(R.array.governorate)
        val arrayAdapter1 = ArrayAdapter(this, R.layout.dropdown_item, governorates)
        binding.autoCompleteTextViewGov.setAdapter(arrayAdapter1)

        val property = resources.getStringArray(R.array.Property_Options)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.dropdown_item, property)
        binding.autoCompleteTextViewProperty.setAdapter(arrayAdapter2)

        val rooms = resources.getStringArray(R.array.Rooms)
        val arrayAdapter3 = ArrayAdapter(this, R.layout.dropdown_item, rooms)
        binding.autoCompleteTextViewRooms.setAdapter(arrayAdapter3)

        val bathrooms = resources.getStringArray(R.array.Bathrooms)
        val arrayAdapter4 = ArrayAdapter(this, R.layout.dropdown_item, bathrooms)
        binding.autoCompleteTextViewBathrooms.setAdapter(arrayAdapter4)

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreatePostBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        val galleryHelper = GalleryHelper(this)
        val databaseManager = FirebaseDatabaseManager(firebaseAuth)
        val viewModelFactory = CreatePostViewModelFactory(galleryHelper , databaseManager)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreatePostViewModel::class.java]
        binding.viewModel = viewModel


        imageAccessPermissionHelper = ImageAccessPermissionHelper(this)


        binding.AddPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!imageAccessPermissionHelper.hasAccessPermission33()) {
                    imageAccessPermissionHelper.requestAccessPermission33(::displayImage)
                } else {
                    displayImage()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (!imageAccessPermissionHelper.hasAccessPermission34()) {
                    imageAccessPermissionHelper.requestAccessPermission34(::displayImage)
                } else {
                    displayImage()
                }
            } else {
                if (!imageAccessPermissionHelper.hasAccessPermission32()) {
                    imageAccessPermissionHelper.requestAccessPermission32(::displayImage)
                } else {
                    displayImage()
                }
            }
        }

        binding.HouseImage1.setOnLongClickListener {
            binding.trash1.visibility = View.VISIBLE
            true
        }

        binding.HouseImage2.setOnLongClickListener {
            binding.trash2.visibility = View.VISIBLE
            true
        }

        binding.HouseImage3.setOnLongClickListener {
            binding.trash3.visibility = View.VISIBLE
            true
        }

        binding.trash1.setOnClickListener {
            binding.HouseImage1.setImageURI(null)
            imageUris.removeAt(0)
        }

        binding.trash2.setOnClickListener {
            binding.HouseImage2.setImageURI(null)
            imageUris.removeAt(0)
        }

        binding.trash3.setOnClickListener {
            binding.HouseImage3.setImageURI(null)
            imageUris.removeAt(0)
        }

        binding.autoCompleteTextViewGov.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.city.value = selectedItem
        }

        binding.autoCompleteTextViewProperty.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.property.value = selectedItem
        }

        binding.autoCompleteTextViewBathrooms.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.numBathrooms.value = selectedItem
        }

        binding.autoCompleteTextViewRooms.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.numRooms.value = selectedItem
        }

        binding.textInputEditTextPrice.setText(viewModel.price.value)

        binding.textInputEditTextArea.setText(viewModel.area.value)

        binding.textInputEditTextDescription.setText(viewModel.description.value)



        binding.Publish.setOnClickListener {
            viewModel.onPublishClicked()
            if(viewModel.postStatus.value==PostStatus.SUCCESS){
                viewModel.storeImages(imageUris)
                val intent = Intent(this , MapActivity::class.java)
                val postId = databaseManager.getPostId()
                val userId = databaseManager.getUserId()
                intent.putExtra("userKey", userId)
                intent.putExtra("postKey", postId)
                startActivity(intent)
            }else{
                Toast.makeText(this,"please fill the empty fields",Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imageAccessPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, ::displayImage
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleGalleryResult(requestCode, resultCode, data, object : GalleryHelper.GalleryCallback {

                override fun onImageSelected(imageUri: String) {
                    val clipData = data?.clipData
                    if (clipData != null) {

                        for (i in 0 until min(clipData.itemCount,3)) {
                            val uri = clipData.getItemAt(i).uri
                            displayImageInImageView(uri, i)
                            imageUris.add(i,clipData.getItemAt(i).uri)
                        }
                    }
                }

                override fun onGalleryCanceled() {
                    onBackPressedDispatcher
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            })
    }

    private fun displayImageInImageView(uri: Uri, index: Int) {
        when (index) {
            0 -> binding.HouseImage1.setImageURI(uri)
            1 -> binding.HouseImage2.setImageURI(uri)
            2 -> binding.HouseImage3.setImageURI(uri)
            // Handle more cases if needed
        }
    }

    private fun displayImage() {
        viewModel.openGallery()
    }
}