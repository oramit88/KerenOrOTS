package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.Controllers.TaskController;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.R;
import il.ac.shenkar.kerenor.tasksapp.Utils.AnalyticsApplication;
import il.ac.shenkar.kerenor.tasksapp.Utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import java.util.List;

/**
 * LoginActivity.java - a class that check if there is a user and if not -
 * sign up as a new manager or log in as an existing user
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class LoginActivity extends AppCompatActivity {
    ParseController parseController;
    TaskController controller;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* google analytics code */
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Login screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        try {
            controller = new TaskController(this);
            parseController = new ParseController(this);
            parseController.initializeParseDB();
        }
        catch (Exception e){
            Log.e("LoginActivity", "parse connection error: " + e.getMessage());
        }

        /* connected user - manager or employee */
        if (parseController.checkCurrentUser()) {
            Intent intent = new Intent(this, TabLayoutActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Log.i("LoginActivity", "User doesn't exist");
            setContentView(R.layout.activity_login);
            getSupportActionBar().setElevation(0); /* remove the shadow from the action bar */
            getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE); /* remove the label from the action bar */

            /* change the headline font */
            Typeface chalkFont = Typeface.createFromAsset(this.getAssets(), "fonts/DK Cool Crayon.ttf");
            TextView username_text = (TextView) findViewById(R.id.username_text);
            TextView password_text = (TextView) findViewById(R.id.password_text);
            TextView phone_number_text = (TextView) findViewById(R.id.phone_number_text);

            username_text.setTypeface(chalkFont);
            password_text.setTypeface(chalkFont);
            phone_number_text.setTypeface(chalkFont);

            /* Login button code */
            final Button loginButton = (Button) this.findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText username = (EditText) findViewById(R.id.username_field);
                    final String username_str = username.getText().toString();
                    EditText password = (EditText) findViewById(R.id.password_field);
                    final String password_str = password.getText().toString();

                    parseController.logInToApp(username_str, password_str, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) { // user logged in successfully
                                boolean isManager = parseController.checkIsManager();

                                if (isManager) { /* manager */
                                    Log.i("LoginActivity", "user logged in as manager");

                                    /* save the current user in shared preferences */
                                    sharedpreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("current_username", username_str);
                                    editor.putString("current_password", password_str);
                                    editor.putInt("notification_interval", -1);
                                    editor.apply(); /* 'apply' instead of 'commit' - do it in background */

                                    if (!parseController.checkIfActivated()) { /* this is the manager's first time */
                                        Intent intent = new Intent(getApplicationContext(), CreateEditTeamActivity.class);
                                        intent.putExtra("requestCode", Constants.CREATE_TEAM);
                                        startActivity(intent);
                                        finish();
                                    } else { /* manager already signed in before */
                                        if (controller.checkIfSqlEmpty()) {
                                            String teamName = ParseUser.getCurrentUser().get("team").toString();
                                            parseController.getAllTasksByTeamFromParse(teamName, new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    if (e == null) {
                                                        List<TaskItem> tasksList = Utils.fromParseListToTasksList(objects);
                                                        controller.overrideSqlDB(tasksList); // send all list to SQLite
                                                    } else {
                                                        Log.e("LoginActivity", "error getting list from parse: " + e.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                        Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else { /* employee */
                                    Log.i("LoginActivity", "user logged in as employee");

                                    /* save the current user in shared preferences */
                                    sharedpreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("notification_interval", -1);
                                    editor.apply(); // apply instead of commit, do it in background

                                    if (controller.checkIfSqlEmpty()) {
                                        String eMail = ParseUser.getCurrentUser().get("email").toString();
                                        parseController.getAllTasksByEMailFromParse(eMail, new FindCallback<ParseObject>() {
                                            @Override
                                            /* enter done only if there's an internet connection */
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                if (e == null) {
                                                    List<TaskItem> tasksList = Utils.fromParseListToTasksList(objects);
                                                    controller.overrideSqlDB(tasksList); // send all list to SQLite
                                                } else {
                                                    Log.e("LoginActivity", "error getting list from parse: " + e.getMessage());
                                                }
                                            }
                                        });
                                    }

                                    if (!parseController.checkIfActivated()) {
                                        String toast = "Youv'e been added successfully to team: " + parseController.getTeamName();
                                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                                        parseController.activatedAccount(); /* change isActivated to true */
                                    }
                                    Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Log.e("Login failed", e.getMessage());
                                String toast = "login failed: " + e.getMessage() + ". Please try again";
                                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            /* signup code */
            final Button signupButton = (Button) this.findViewById(R.id.signup_button);
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText username = (EditText) findViewById(R.id.username_field);
                    final String username_str = username.getText().toString();
                    EditText password = (EditText) findViewById(R.id.password_field);
                    final String password_str = password.getText().toString();
                    EditText phone_number = (EditText) findViewById(R.id.phone_number_field);
                    String phone_number_str = phone_number.getText().toString();

                    if (Utils.isValidEmail(username_str)) {
                        parseController.signUpToAppManager(username_str, password_str, phone_number_str, new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) { /* user sign up successfully (for the first time) */
                                /* save the current user in shared preferences */
                                    sharedpreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("current_username", username_str);
                                    editor.putString("current_password", password_str);
                                    editor.putInt("notification_interval", -1);
                                    editor.apply();

                                    Log.i("LoginActivity", "manager created");
                                    Intent intent = new Intent(getApplicationContext(), CreateEditTeamActivity.class);
                                    intent.putExtra("requestCode", Constants.CREATE_TEAM);
                                    startActivity(intent);
                                    finish();
                                } else {   // Sign up didn't succeed - user already in DB
                                    Log.e("LoginActivity", "username already exists");
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Username (eMail) is not valid. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /* hide the keyboard when user touch the screen */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }






}
