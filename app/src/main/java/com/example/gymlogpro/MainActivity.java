package com.example.gymlogpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gymlogpro.db.AppDatabase;
import com.example.gymlogpro.db.GymLogDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String USER_ID_KEY = "com.example.gymlogpro.userIdKey";
    private static final String PREFS = "com.example.gymlogpro.prefs";
    private TextView mMainDisplay;
    private EditText mExercise;
    private EditText mWeight;
    private EditText mReps;
    private Button mSubmitButton;

    private GymLogDao mGymLogDao;
    private List<GymLog> mGymLogs;

    private int mUserId = -1;

    private SharedPreferences mPreferences = null;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDatabase();
        wireUpDisplay();
        checkForUser();
        addUserToPreferences(mUserId);
        loginUser(mUserId);

        refreshDisplay();
    }

    private void loginUser(int mUserId) {
        mUser = mGymLogDao.getUserByUserId(mUserId);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        logoutUser();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mUser != null) {
            MenuItem item = menu.findItem(R.id.userMenuLogout);
            item.setTitle(mUser.getUsername());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void checkForUser() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);

        if(mUserId != -1) {
            return;
        }

        if(mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);

        if(mUserId != -1) {
            return;
        }

        List<User> users = mGymLogDao.getAllUsers();
        if(users.size() <= 0) {
            User defaultUser = new User("testUser1", "testUser123");
            User altUser = new User("testUser2", "testUser456");
            mGymLogDao.insert(defaultUser, altUser);
        }

        Intent intent = LoginActivity.intentFactory(this);
        startActivity(intent);
    }

    private void getPrefs() {
        mPreferences = this.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private void wireUpDisplay() {

        mMainDisplay = findViewById(R.id.mainGymLogDisplay);
        mMainDisplay.setMovementMethod(new ScrollingMovementMethod());
        mExercise = findViewById(R.id.mainExerciseEditText);
        mWeight = findViewById(R.id.mainWeightEditText);
        mReps = findViewById(R.id.mainRepsEditText);
        mSubmitButton = findViewById(R.id.mainSubmitButton);

        mSubmitButton.setOnClickListener(v -> {

            GymLog gymLog = getValuesFromDisplay();
            mGymLogDao.insert(gymLog);
            refreshDisplay();
        });
    }

    private void getDatabase() {

        mGymLogDao = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getGymLogDao();
    }

    private GymLog getValuesFromDisplay() {

        String exercise = "No record found";
        double weight = 0.0;
        int reps = 0;

        exercise = mExercise.getText().toString();

        try {
            weight = Double.parseDouble(mWeight.getText().toString());
        } catch (NumberFormatException nfe) {
            Log.d("GYM_LOG", "Couldn't convert weight");
        }

        try {
            reps = Integer.parseInt(mReps.getText().toString());
        } catch (NumberFormatException nfe) {
            Log.d("GYM_LOG", "Couldn't convert reps");
        }

        return new GymLog(exercise, weight, reps, mUserId);
    }

    private void refreshDisplay() {
        mGymLogs = mGymLogDao.getGymLogsByUserId(mUserId);

        if(mGymLogs.size() == 0) {
            mMainDisplay.setText(R.string.noLogsMessage);
        } else {
            StringBuilder sb = new StringBuilder();
            for (GymLog log : mGymLogs) {
                sb.append(log);
                sb.append("\n=========================\n");
            }
            mMainDisplay.setText(sb.toString());
        }
    }

    public static Intent intentFactory(Context context, int mUserId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID_KEY, mUserId);
        return intent;
    }

    private void addUserToPreferences(int mUserId) {
        if(mPreferences == null) {
            getPrefs();
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(USER_ID_KEY, mUserId);
        editor.apply();
    }

    private void logoutUser() {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage(R.string.logout);

        alertBuilder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            clearUserFromIntent();
            clearUserFromPrefs();
            mUserId = -1;
            checkForUser();
        });

        alertBuilder.setNegativeButton(R.string.no, (dialogInterface, i) -> {

        });

        alertBuilder.setOnCancelListener(dialog -> {

        });

        AlertDialog goodAlert = alertBuilder.create();
        goodAlert.show();
    }

    private void clearUserFromPrefs() {
        addUserToPreferences(-1);
    }

    private void clearUserFromIntent() {
        getIntent().putExtra(USER_ID_KEY, -1);
    }
}