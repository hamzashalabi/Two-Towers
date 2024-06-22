package eu.application.twotowers.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import eu.application.twotowers.R
import eu.application.twotowers.onboardingscreen.OnboardingActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        val splashLiveData = MutableLiveData<Unit>()

        val job = CoroutineScope(Dispatchers.Main).launch {
            // Perform any initialization (data fetching etc.)
            delay(0) // Delay in milliseconds
            splashLiveData.postValue(Unit)
        }
        splashLiveData.observe(this) {
            startActivity(Intent(this@SplashScreen, OnboardingActivity::class.java))
            finish()
        }
    }
}