package eu.application.twotowers.onboardingscreen

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class OnboardingViewModel : ViewModel() {
    private val onboardingPages = listOf(
        OnboardingPage("Two Towers", "Explore the houses near you with a simple tap.","Map"),
        OnboardingPage("Two Towers", "Find houses with the details you wish for.","Explore"),
        OnboardingPage("Two Towers", "Connect easily with other users on this app.","Chat")
    )

    val pages: LiveData<List<OnboardingPage>> = MutableLiveData(onboardingPages)




    fun isOnboardingCompleted(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("isOnboardingCompleted", false)
    }



}