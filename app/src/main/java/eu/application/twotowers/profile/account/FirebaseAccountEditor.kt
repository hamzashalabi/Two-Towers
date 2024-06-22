package eu.application.twotowers.profile.account

interface FirebaseAccountEditor {
fun changePassword(oldPassword: String , email: String , newPassword: String)
fun changeName(newName: String)
fun deleteAccount(email: String , password: String)
fun signOut()
}