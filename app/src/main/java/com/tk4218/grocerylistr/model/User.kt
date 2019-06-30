package com.tk4218.grocerylistr.model

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.tk4218.grocerylistr.Database.PasswordStorage
import com.tk4218.grocerylistr.LoginCallback
import com.tk4218.grocerylistr.NewAccountCallback
import com.tk4218.grocerylistr.NewAccountCallback.ErrorField
import com.tk4218.grocerylistr.R


class User() {
    var userName: String = ""
    var password: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var email: String = ""

    constructor(userName: String, password: String, firstName: String, lastName: String, email: String) : this() {
        this.userName = userName
        this.password = password
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }

    fun save() {
        val userRef = FirebaseDatabase.getInstance().getReference("user/$userName")
        userRef.setValue(this)
    }

    companion object {
        fun login(username: String, password: String, loginCallback: LoginCallback) {
            when(true) {
                username.isEmpty() ->
                    loginCallback.onLoginFailure(LoginCallback.ErrorField.USERNAME, R.string.error_field_required)
                password.isEmpty() ->
                    loginCallback.onLoginFailure(LoginCallback.ErrorField.PASSWORD, R.string.error_field_required)
                password.isNotEmpty() && !isPasswordValid(password) ->
                    loginCallback.onLoginFailure(LoginCallback.ErrorField.PASSWORD, R.string.error_invalid_password)
                else -> {
                    val userRef = FirebaseDatabase.getInstance().getReference("user/$username")
                    userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.value != null) {
                                val user = dataSnapshot.getValue(User::class.java)!!
                                if(PasswordStorage.verifyPassword(password, user.password)){
                                    loginCallback.onLoginSuccess(user)
                                } else {
                                    loginCallback.onLoginFailure(LoginCallback.ErrorField.PASSWORD, R.string.error_incorrect_password)
                                }
                            } else {
                                loginCallback.onLoginFailure(LoginCallback.ErrorField.USERNAME, R.string.error_invalid_username)
                            }
                        }
                        override fun onCancelled(dataSnapshot: DatabaseError) {
                            loginCallback.onLoginFailure(LoginCallback.ErrorField.USERNAME, 0)
                        }
                    })
                }
            }
        }

        fun logout(context: Context) {
            val settings = context.getSharedPreferences("application_settings", Context.MODE_PRIVATE)
            settings.edit().putString("user", "").apply()
        }
        private fun isPasswordValid(password: String): Boolean {
            return password.length > 4
        }

        fun currentUser(context: Context): User {
            val settings = context.getSharedPreferences("application_settings", Context.MODE_PRIVATE)
            val user = settings.getString("user", "") ?: ""
            if(user.isEmpty()) return User()
            return Gson().fromJson(user, User::class.java)
        }

        fun currentUsername(context: Context): String {
            return currentUser(context).userName
        }

        fun setCurrentUser(context: Context, user: User) {
            val settings = context.getSharedPreferences("application_settings", Context.MODE_PRIVATE)
            settings.edit().putString("user", Gson().toJson(user)).apply()
        }

        fun createNewUser(username: String,
                          password: String,
                          passwordVerify: String,
                          email: String,
                          firstName: String,
                          lastName: String,
                          callback: NewAccountCallback) {
            when(true){
                username.isEmpty() ->
                    callback.onAccountCreateFailure(ErrorField.USERNAME, "Please add a username.")
                password.isEmpty() ->
                    callback.onAccountCreateFailure(ErrorField.PASSWORD, "Please create a password.")
                passwordVerify.isEmpty() ->
                    callback.onAccountCreateFailure(ErrorField.PASSWORD_VERIFY, "Please verify password.")
                password != passwordVerify ->
                    callback.onAccountCreateFailure(ErrorField.PASSWORD_VERIFY, "Passwords do not match.")
                email.isEmpty() ->
                    callback.onAccountCreateFailure(ErrorField.EMAIL, "Please add an email.")
                else -> {
                    val userRef = FirebaseDatabase.getInstance().getReference("user/$username")
                    userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.value == null) {
                                try {
                                    val passwordHash = PasswordStorage.createHash(password)
                                    if (passwordHash != "") {
                                        val user = User(username, passwordHash, firstName, lastName, email)
                                        user.save()
                                        callback.onAccountCreated(user)
                                    } else {
                                        callback.onAccountCreateFailure(ErrorField.GENERAL_ERROR, "There was an error creating this account.")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    callback.onAccountCreateFailure(ErrorField.GENERAL_ERROR, "There was an error creating this account.")
                                }
                            } else {
                                callback.onAccountCreateFailure(ErrorField.USERNAME, "An account with this username already exists.")
                            }
                        }
                        override fun onCancelled(dataSnapshot: DatabaseError) {
                            callback.onAccountCreateFailure(ErrorField.GENERAL_ERROR, "There was an error creating this account.")
                        }
                    })
                }
            }
        }
    }
}