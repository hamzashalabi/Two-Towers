package eu.application.twotowers.registration

interface AuthenticationManager {
        fun login(email: String , password: String , callback:(success:Boolean)->Unit)
        fun registration(userName: String , email: String , password: String , callback:(success:Boolean)->Unit)
        fun userImage(userImage :String)
}