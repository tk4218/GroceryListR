package com.tk4218.grocerylistr;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tk4218.grocerylistr.Database.JSONResult;
import com.tk4218.grocerylistr.Database.PasswordStorage;
import com.tk4218.grocerylistr.Database.QueryBuilder;

public class NewAccountActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mPasswordVerify;
    private EditText mEmail;
    private EditText mFirstName;
    private EditText mLastName;

    private static final int ACCOUNT_CREATED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        mUsername = findViewById(R.id.new_username);
        mPassword = findViewById(R.id.new_password);
        mPasswordVerify = findViewById(R.id.new_password_verify);
        mEmail = findViewById(R.id.new_email);
        mFirstName = findViewById(R.id.new_first_name);
        mLastName = findViewById(R.id.new_last_name);
        Button createAccount = findViewById(R.id.button_create_account);

        mPasswordVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_ACTION_DONE) {
                    if(!mPassword.getText().toString().equals("")){
                        if(!mPasswordVerify.getText().toString().equals(mPassword.getText().toString())){
                            mPasswordVerify.setError("Passwords do not match.");
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateAccount().execute();
            }
        });
    }

    private class CreateAccount extends AsyncTask<Void, Void, boolean[]> {
        private QueryBuilder mQb = new QueryBuilder();
        @Override
        protected boolean[] doInBackground(Void... voids) {
            boolean[] errors = new boolean[8];
            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            String passwordVerify = mPasswordVerify.getText().toString();
            String email = mEmail.getText().toString();
            String firstName = mFirstName.getText().toString();
            String lastName = mLastName.getText().toString();

            if(username.equals("")){
                errors[0] = true;
                return errors;
            }
            if(password.equals("")){
                errors[1] = true;
                return errors;
            }
            if(passwordVerify.equals("")){
                errors[2] = true;
                return errors;
            }
            if(!password.equals(passwordVerify)){
                errors[3] = true;
                return errors;
            }
            if(email.equals("")){
                errors[4] = true;
                return errors;
            }

            JSONResult existingUser = mQb.getUserByUsername(username);
            if(existingUser.getCount() > 0) {
                errors[5] = true;
                return errors;
            }
            existingUser = mQb.getUserByEmail(email);
            if(existingUser.getCount() > 0) {
                errors[6] = true;
                return errors;
            }

            try {
                String passwordHash = PasswordStorage.createHash(password);
                if(!passwordHash.equals("")){
                    if(!mQb.insertUser(username, email, passwordHash, firstName, lastName)){
                        errors[7] = true;
                    }
                }else{
                    errors[7] = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors[7] = true;
            }
            return errors;
        }

        @Override
        protected void onPostExecute(boolean[] result) {
            if(result[0]){
                mUsername.setError("Please add a username.");
                mUsername.requestFocus();
            }else if(result[1]){
                mPassword.setError("Please create a password.");
                mPassword.requestFocus();
            }else if(result[2]){
                mPasswordVerify.setError("Please verify password.");
                mPasswordVerify.requestFocus();
            }else if(result[3]){
                mPasswordVerify.setError("Passwords do not match.");
                mPasswordVerify.requestFocus();
            }else if(result[4]) {
                mEmail.setError("Please add an email.");
                mEmail.requestFocus();
            }else if(result[5]) {
                mUsername.setError("This username already exists.");
                mUsername.requestFocus();
            }else if(result[6]) {
                mEmail.setError("An account with this email already exists.");
                mEmail.requestFocus();
            }else if(result[7]) {
                Toast.makeText(NewAccountActivity.this, "There was an error adding the account.", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent();
                intent.putExtra("Username", mUsername.getText().toString());
                setResult(ACCOUNT_CREATED, intent);
                finish();
            }
        }
    }
}
