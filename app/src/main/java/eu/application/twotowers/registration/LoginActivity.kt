package eu.application.twotowers.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.R
import eu.application.twotowers.databinding.LoginpageBinding
import eu.application.twotowers.databinding.LoginpageBindingImpl
import eu.application.twotowers.map.MapActivity

class LoginActivity:AppCompatActivity(){

    private lateinit var binding : LoginpageBinding
    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginpageBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)


    // Animation content
    // left to right animation
        val animationCircle1 = AnimationUtils.loadAnimation(this, R.anim.circle_move)
        binding.AniCircle1.startAnimation(animationCircle1)


    // right to left animations
        val animationCircle2 = AnimationUtils.loadAnimation(this, R.anim.circle_move2)
        binding.AniCircle2.startAnimation(animationCircle2)


    // Loading screen Animations Creation
        val animationLoading = AnimationUtils.loadAnimation(this , R.anim.loading_screen)



    // initializing view model
        val firebaseAuth = FirebaseAuth.getInstance() // Obtain instance of FirebaseAuth
        val authManager = FirebaseAuthenticationManger(firebaseAuth)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(authManager))[LoginViewModel::class.java]


    // bind view model
        binding.viewModel = viewModel


    // bind screen content (Edit texts and buttons)
        binding.editTextTextEmailAddress.setText(viewModel.email.value)

        binding.editTextTextPassword.setText(viewModel.password.value)

        binding.buttonLogin.setOnClickListener{

            // Loading screen Animations Initialization
            binding.AniCircle1.startAnimation(animationLoading)
            binding.AniCircle2.startAnimation(animationLoading)


            viewModel.onLoginClicked()
        }

    // Login in results
        viewModel.loginStatus.observe(this){status->
            when (status){
                LoginStatus.SUCCESS->{
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                LoginStatus.FAILURE->{
                    binding.AniCircle1.startAnimation(animationCircle1)
                    binding.AniCircle2.startAnimation(animationCircle2)


                    binding.editTextTextEmailAddress.setText("")
                    binding.editTextTextPassword.setText("")
                }
            }
        }


    // move to sign up page
        binding.textView5.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}

