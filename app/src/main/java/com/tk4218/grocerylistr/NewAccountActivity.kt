package com.tk4218.grocerylistr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast

import com.tk4218.grocerylistr.model.User

import kotlinx.android.synthetic.main.activity_new_account.*

interface NewAccountCallback {
    enum class ErrorField{
        USERNAME, PASSWORD, PASSWORD_VERIFY, EMAIL, GENERAL_ERROR
    }
    fun onAccountCreated(user: User)
    fun onAccountCreateFailure(errorField: ErrorField, error: String)
}

class NewAccountActivity : AppCompatActivity(), NewAccountCallback {
    companion object {
        private const val ACCOUNT_CREATED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)

        new_password_verify.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_ACTION_DONE) {
                if (new_password.text.toString() != "") {
                    if (new_password_verify.text.toString() != new_password.text.toString()) {
                        new_password_verify.error = "Passwords do not match."
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        button_create_account.setOnClickListener {
            User.createNewUser(new_username.text.toString(),
                    new_password.text.toString(),
                    new_password_verify.text.toString(),
                    new_email.text.toString(),
                    new_first_name.text.toString(),
                    new_last_name.text.toString(),
                    this)
        }
    }

    override fun onAccountCreated(user: User) {
        User.setCurrentUser(this, user)
        val intent = Intent()
        setResult(ACCOUNT_CREATED, intent)
        finish()
    }

    override fun onAccountCreateFailure(errorField: NewAccountCallback.ErrorField, error: String) {
        when(errorField) {
            NewAccountCallback.ErrorField.USERNAME -> {
                new_username.error = error
                new_username.requestFocus()
            }
            NewAccountCallback.ErrorField.PASSWORD -> {
                new_password.error = error
                new_password.requestFocus()
            }
            NewAccountCallback.ErrorField.PASSWORD_VERIFY -> {
                new_password_verify.error = error
                new_password_verify.requestFocus()
            }
            NewAccountCallback.ErrorField.EMAIL -> {
                new_email.error = error
                new_email.requestFocus()
            }
            NewAccountCallback.ErrorField.GENERAL_ERROR -> {
                Toast.makeText(this, "There was an error adding the account.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
