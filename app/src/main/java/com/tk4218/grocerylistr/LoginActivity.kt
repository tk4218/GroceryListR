package com.tk4218.grocerylistr

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.tk4218.grocerylistr.Database.QueryBuilder
import com.tk4218.grocerylistr.model.User

import java.util.ArrayList

import org.json.JSONException

import kotlinx.android.synthetic.main.activity_login.*

interface LoginCallback {
    enum class ErrorField {
        USERNAME, PASSWORD
    }
    fun onLoginSuccess(user: User)
    fun onLoginFailure(errorField: ErrorField, errorId: Int)
}

class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor>, LoginCallback {
    private var mFacebookCallback: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //See if already logged in and Username is not blank
        if (User.currentUsername(this).isNotEmpty()) {
            Log.d("LoginActivity", "Logged in with username: ${User.currentUsername(this)}")
            goToMainActivity()
        }

        /*------------------------------------------------------------
         * Facebook Login
         *------------------------------------------------------------*/
        mFacebookCallback = CallbackManager.Factory.create()
        facebook_login_button.setReadPermissions(listOf(EMAIL))
        facebook_login_button.registerCallback(mFacebookCallback, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { `object`, _ ->
                    try {
                        val userId = `object`.getString("id")
                        val email = `object`.getString("email")
                        val name = `object`.getString("name")

                        showProgress(true)
                        val facebookLogin = FacebookLogin(userId, email, name)
                        facebookLogin.execute()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                request.parameters = parameters
                request.executeAsync()
            }
            override fun onCancel() {}
            override fun onError(exception: FacebookException) {}
        })

        login_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }
        button_new_account.setOnClickListener {
            val intent = Intent(this@LoginActivity, NewAccountActivity::class.java)
            startActivityForResult(intent, REQUEST_CREATE_ACCOUNT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookCallback?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CREATE_ACCOUNT) {
            if (resultCode == ACCOUNT_CREATED) {
                goToMainActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onLoginSuccess(user: User) {
        showProgress(false)
        User.setCurrentUser(this, user)
        goToMainActivity()
    }
    override fun onLoginFailure(errorField: LoginCallback.ErrorField, errorId: Int) {
        showProgress(false)
        when(errorField) {
            LoginCallback.ErrorField.USERNAME -> {
                login_username.error = getString(errorId)
                login_username.requestFocus()
            }
            LoginCallback.ErrorField.PASSWORD -> {
                login_password.error = getString(errorId)
                login_password.requestFocus()
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        login_username.error = null
        login_password.error = null

        showProgress(true)
        User.login(login_username.text.toString(), login_password.text.toString(), this)
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                login_form.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                login_progress.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {
    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        login_username.setAdapter(adapter)
    }


    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }

    private inner class FacebookLogin internal constructor(private var mUserId: String?, private val mEmail: String, private val mName: String) : AsyncTask<Void, Void, Void>() {
        private val mQb = QueryBuilder()
        private var mFirstName: String? = null
        private var mLastName: String? = null

        override fun doInBackground(vararg voids: Void): Void? {
            var existingUser = mQb.getUserByUsername(mUserId)
            if (existingUser.count == 0) {
                existingUser = mQb.getUserByEmail(mEmail)
            }
            if (existingUser.count == 0) {
                val names = mName.split(" ".toRegex(), 2).toTypedArray()
                mFirstName = names[0]
                mLastName = if (names.size > 1) names[1] else ""
                mQb.insertUser(mUserId, mEmail, "", mFirstName, mLastName)
            } else {
                mUserId = existingUser.getString("Username")
                mFirstName = existingUser.getString("FirstName")
                mLastName = existingUser.getString("LastName")
            }
            return null
        }

        override fun onPostExecute(aVoid: Void) {
            showProgress(false)
            //mSettings!!.login(mUserId, mFirstName, mLastName)
            goToMainActivity()
        }
    }

    companion object {
        //New Account Activity Intent values
        private val REQUEST_CREATE_ACCOUNT = 0
        private val ACCOUNT_CREATED = 1

        //Facebook Login variables
        private val EMAIL = "email"
    }
}

