package com.example.gymlogpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gymlogpro.db.AppDatabase;
import com.example.gymlogpro.db.GymLogDao;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameField;
    private EditText mPasswordField;

    private Button mButton;

    private GymLogDao mGymLogDao;

    private String mUsername, mPassword;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wireUpDisplay();
        getDatabase();

    }

    private void wireUpDisplay() {

        mUsernameField = findViewById(R.id.et_login_username);
        mPasswordField = findViewById(R.id.et_login_password);
        mButton = findViewById(R.id.btn_login);

        mButton.setOnClickListener(v -> {
            getValuesFromDisplay();
            if(checkForUserInDatabase()) {
                if(!validatePassword()) {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = MainActivity.intentFactory(getApplicationContext(), mUser.getUserId());
                    startActivity(intent);
                }
            }
        });
    }

    private void getValuesFromDisplay() {

        mUsername = mUsernameField.getText().toString();
        mPassword = mPasswordField.getText().toString();
    }

    private boolean checkForUserInDatabase() {

        mUser = mGymLogDao.getUserByUsername(mUsername);
        if(mUser == null) {
            Toast.makeText(this, "no user " + mUsername + " found", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {

        return mUser.getPassword().equals(mPassword);
    }

    private void getDatabase() {

        mGymLogDao = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getGymLogDao();
    }

    public static Intent intentFactory(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }
}