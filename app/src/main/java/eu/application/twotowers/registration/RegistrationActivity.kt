package eu.application.twotowers.registration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.R
import eu.application.twotowers.databinding.RegistrationpageBinding
import eu.application.twotowers.databinding.RegistrationpageBindingImpl
import eu.application.twotowers.map.MapActivity


class RegistrationActivity:AppCompatActivity() {

    private lateinit var binding : RegistrationpageBinding
    private lateinit var viewModel : RegistrationViewModel
    private lateinit var registrationImageAccessPermissionHelper: RegistrationImageAccessPermissionHelper
    var userImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationpageBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        // Animation content
        // left to right animation
        val animationCircle1 = AnimationUtils.loadAnimation(this, R.anim.circle_move)
        binding.AniCircle1.startAnimation(animationCircle1)

        // right to left animations
        val animationCircle2 = AnimationUtils.loadAnimation(this, R.anim.circle_move2)
        binding.AniCircle2.startAnimation(animationCircle2)

        // Loading screen Animations
        val animationLoading = AnimationUtils.loadAnimation(this , R.anim.loading_screen)

        val firebaseAuth = FirebaseAuth.getInstance() // Obtain instance of FirebaseAuth
        val authManager = FirebaseAuthenticationManger(firebaseAuth)
        val registerGalleryHelper = RegisterGalleryHelper(this)
        val registrationViewModelFactory = RegistrationViewModelFactory(authManager , registerGalleryHelper)
        viewModel = ViewModelProvider(this, registrationViewModelFactory)[RegistrationViewModel::class.java]

        binding.viewModel = viewModel

        registrationImageAccessPermissionHelper = RegistrationImageAccessPermissionHelper(this)

        binding.editTextTextEmailAddress.setText(viewModel.email.value)

        binding.editTextTextPassword.setText(viewModel.password.value)

        binding.editTextUserName.setText(viewModel.userName.value)

        binding.alreadyHaveAnAccount.setOnClickListener {
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
        }

        binding.addImageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!registrationImageAccessPermissionHelper.hasAccessPermission33()) {
                    registrationImageAccessPermissionHelper.requestAccessPermission33(::displayImage)
                } else {
                    displayImage()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (!registrationImageAccessPermissionHelper.hasAccessPermission34()) {
                    registrationImageAccessPermissionHelper.requestAccessPermission34(::displayImage)
                } else {
                    displayImage()
                }
            } else {
                if (!registrationImageAccessPermissionHelper.hasAccessPermission32()) {
                    registrationImageAccessPermissionHelper.requestAccessPermission32(::displayImage)
                } else {
                    displayImage()
                }
            }
        }



        binding.buttonSignUp.setOnClickListener{
            binding.AniCircle1.startAnimation(animationLoading)
            binding.AniCircle2.startAnimation(animationLoading)
            viewModel.onSignedUpClicked()
        }

        viewModel.registrationStatus.observe(this){status->
            when(status){
                RegistrationStatus.SUCCESS -> {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()
                    viewModel.saveUserImage(userImage)
                }
                RegistrationStatus.FAILURE -> {
                    binding.AniCircle1.startAnimation(animationCircle1)
                    binding.AniCircle2.startAnimation(animationCircle2)


                    binding.editTextTextEmailAddress.setText("")
                    binding.editTextTextPassword.setText("")
                    binding.editTextUserName.setText("")
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        registrationImageAccessPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, ::displayImage
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleGalleryResult(requestCode, resultCode, data, object : RegisterGalleryHelper.GalleryCallbackManger {

            override fun onImageSelected(imageUri: String) {
                Glide.with(this@RegistrationActivity).load(imageUri).into(binding.addImageButton)
                userImage = imageUri
            }

            override fun onGalleryCanceled() {
                onBackPressedDispatcher
            }
        })
    }

    private fun displayImage() {
        viewModel.openGallery()
    }
}


