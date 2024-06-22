package eu.application.twotowers.onboardingscreen


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import eu.application.twotowers.R
import eu.application.twotowers.databinding.ActivityOnboardingBindingImpl
import eu.application.twotowers.registration.LoginActivity


class OnboardingActivity: AppCompatActivity() {
    private lateinit var viewModel: OnboardingViewModel
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[OnboardingViewModel::class.java]

        if (viewModel.isOnboardingCompleted(this)) {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Show onboarding screens
            val binding = DataBindingUtil.setContentView<ActivityOnboardingBindingImpl>(this, R.layout.activity_onboarding)
            binding.viewModel = viewModel



            viewPager = findViewById(R.id.view_pager)

            viewModel.pages.observe(this) { onboardingPages ->
                // Create an adapter with the observed data
                val adapter = OnboardingPagerAdapter(this, onboardingPages){
                    markOnboardingAsCompleted()
                }
                viewPager.adapter = adapter
            }
            // Setup ViewPager and adapter
        }
    }



        private fun markOnboardingAsCompleted() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isOnboardingCompleted", true)
            editor.apply()
        }
    }
