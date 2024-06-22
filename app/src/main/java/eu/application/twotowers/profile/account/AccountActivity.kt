package eu.application.twotowers.profile.account

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import eu.application.twotowers.R
import eu.application.twotowers.databinding.EditProfileActivityBinding
import eu.application.twotowers.databinding.EditProfileActivityBindingImpl
import eu.application.twotowers.registration.LoginActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var binding : EditProfileActivityBinding
    private lateinit var viewModel : AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileActivityBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAccountEditorManager = FirebaseAccountEditorManager()
        val accountViewModelFactory = AccountViewModelFactory(firebaseAccountEditorManager)
        viewModel = ViewModelProvider(this, accountViewModelFactory)[AccountViewModel::class.java]

        binding.editProfileButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        binding.changePasswordButton.setOnClickListener {
            alertDialogChangePassword(this)
        }

        binding.changeNameButton.setOnClickListener {
           alertDialogChangeUserName(this)
        }

        binding.deleteAccountButton.setOnClickListener {
            alertDialogDeleteAccount(this)
        }

        binding.signOutButton.setOnClickListener {
            alertDialogSignOut(this)
        }
    }

    private fun alertDialogSignOut(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to Sign Out?")
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.signOut()
            viewModel.nav(this , LoginActivity::class.java)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun alertDialogChangePassword(context: Context){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.alert_dialog_change_password , null)
        builder.setTitle("Change Password")

        builder.setView(customView)
        val dialog =builder.create()
        dialog.show()

        val oldPassword = customView.findViewById<EditText>(R.id.editTextTextPassword).text
        val newPassword = customView.findViewById<EditText>(R.id.editTextTextPasswordAgain).text
        val email = customView.findViewById<EditText>(R.id.editTextTextEmailAddress).text
        val okButton = customView.findViewById<Button>(R.id.button)


        okButton.setOnClickListener {
            Log.e("TAG", "alertDialogChangePassword: $oldPassword / $newPassword / $email")
            viewModel.changePassword(oldPassword.toString(),email.toString(), newPassword.toString())
            dialog.dismiss()
        }
    }


    private fun alertDialogChangeUserName(context: Context){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.alert_dialog_change_user_name , null)
        builder.setTitle("Change Email")

        val userName = customView.findViewById<EditText>(R.id.change_user_name_text_view).text
        val okButton = customView.findViewById<Button>(R.id.ok_button)

        builder.setView(customView)
        val dialog =builder.create()
        dialog.show()

        okButton.setOnClickListener {
            viewModel.changeUserName(userName.toString())
            dialog.dismiss()
        }
    }


    private fun alertDialogDeleteAccount(context: Context){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.alert_dialog_delete_account , null)
        builder.setTitle("Delete Account")

        val email = customView.findViewById<EditText>(R.id.editTextTextEmailAddress).text
        val password = customView.findViewById<EditText>(R.id.editTextTextPassword).text
        val okButton = customView.findViewById<Button>(R.id.button)

        builder.setView(customView)
        val dialog =builder.create()
        dialog.show()

        okButton.setOnClickListener {
            viewModel.deleteAccount(email.toString(), password.toString())
            viewModel.nav(this , LoginActivity::class.java)
            dialog.dismiss()
        }
    }
}