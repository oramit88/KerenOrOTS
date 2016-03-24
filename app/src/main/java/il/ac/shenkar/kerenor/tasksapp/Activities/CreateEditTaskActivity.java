package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.Fragments.DatePickerFragment;
import il.ac.shenkar.kerenor.tasksapp.R;
import il.ac.shenkar.kerenor.tasksapp.Utils.AnalyticsApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * CreateEditTaskActivity.java - a class that let the manager create or edit tasks
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class CreateEditTaskActivity extends AppCompatActivity {
    int taskId;
    List<String> teamMembersArray;
    ArrayAdapter<String> teamMembersAdapter;
    ParseController parseController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_task);

        teamMembersArray = new ArrayList<>();
        parseController = new ParseController(this);

        /* cancel the task done picture until the task is actually done by member */
        ImageView task_picture = (ImageView) findViewById(R.id.task_picture);
        task_picture.setVisibility(View.GONE);

        /* google analytics code */
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Create / Edit task screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        final AutoCompleteTextView memberNameAuto = (AutoCompleteTextView) findViewById(R.id.task_member_field);

        /*  get all existing team members from parse */
        parseController = new ParseController(this);
        parseController.getMembersFromParse(parseController.getTeamName(), new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser user : objects) {
                        teamMembersArray.add(user.getUsername());
                    }

                    /* set an adapter for the auto-complete username */
                    teamMembersAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.array_adapter_autocompete_item, teamMembersArray);
                    memberNameAuto.setAdapter(teamMembersAdapter);
                } else {
                    Log.e("edit task activity", "wasn't found: " + e.getMessage());
                }
            }
        });

        final Button taskButton = (Button) this.findViewById(R.id.create_task_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.create_task_title);

        /* change the headline font */
        Typeface chalkFont = Typeface.createFromAsset(this.getAssets(), "fonts/DK Cool Crayon.ttf");
        toolbar_title.setTypeface(chalkFont);

        ImageButton backImageButton = (ImageButton)findViewById(R.id.toolbar_back_button);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /* category - combo box */
        Spinner spinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<TaskCategory> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, TaskCategory.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final EditText dueTimeStr = (EditText) findViewById(R.id.due_time_field);
        dueTimeStr.setText(dateFormat.format(calendar.getTime()));

        /* calendar button listener - open the calendar */
        final ImageView v = (ImageView) findViewById(R.id.calender_create_task_button);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        /* until here - initialize components on screen */

        /* get an intent from the user - create or edit */
        Intent intentTaskDetails = getIntent();
        int requestCode = intentTaskDetails.getExtras().getInt("requestCode");

        // get the existing task details
        if (requestCode == Constants.UPDATE_TASK) {
            Log.i("createTaskActivity", "UPDATE_TASK");

            String taskName = intentTaskDetails.getStringExtra(Constants.TASK_NAME_TEXT);
            String memberName = intentTaskDetails.getStringExtra(Constants.MEMBER_NAME_TEXT);
            String taskLocation = intentTaskDetails.getStringExtra(Constants.TASK_LOCATION_TEXT);
            String taskDueDate = intentTaskDetails.getStringExtra(Constants.DUE_TIME_TEXT);
            String taskCategory = intentTaskDetails.getStringExtra(Constants.CATEGORY_SPINNER);
            String taskPriority = intentTaskDetails.getStringExtra(Constants.PRIORITY_RADIO);
            taskId = intentTaskDetails.getExtras().getInt("taskId");

            parseController.checkIfTaskDone(taskId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) { /* if task is done - we need an image to show */
                        ParseFile taskImageParse = (ParseFile) object.get("taskImage");

                        final ImageView task_picture = (ImageView) findViewById(R.id.task_picture);
                        task_picture.setVisibility(View.VISIBLE);

                        if (taskImageParse != null) { /* take an image from parse */
                            parseController.getImageFromParse(taskImageParse, new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        task_picture.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        } else { /* take a default image from local library */
                            Log.i("edit task", "default picture");
                            task_picture.setImageResource(R.drawable.default_image);
                        }
                    }
                }
            });

            EditText taskNameField = (EditText) findViewById(R.id.task_name_field);
            taskNameField.setText(taskName);
            memberNameAuto.setText(memberName);
            EditText taskLocationField = (EditText) findViewById(R.id.location_field);
            taskLocationField.setText(taskLocation);
            dueTimeStr.setText(taskDueDate);

            /* priority - radio group */
            switch (taskPriority) {
                case "NORMAL": {
                    RadioButton normalRadioButton = (RadioButton) findViewById(R.id.NormalRadioButton);
                    normalRadioButton.setChecked(true);
                    break;
                }
                case "URGENT": {
                    RadioButton urgentRadioButton = (RadioButton) findViewById(R.id.UrgentRadioButton);
                    urgentRadioButton.setChecked(true);
                    break;
                }
                case "LOW": {
                    RadioButton lowRadioButton = (RadioButton) findViewById(R.id.LowRadioButton);
                    lowRadioButton.setChecked(true);
                    break;
                }
            }

            /* category - combo box */
            int categoryLocation = -1; /* initialize combo box location */

            switch (taskCategory) {
                case "CLEANING": {
                    categoryLocation = 0;
                    break;
                }
                case "ELECTRICITY": {
                    categoryLocation = 1;
                    break;
                }
                case "COMPUTERS": {
                    categoryLocation = 2;
                    break;
                }
                case "GENERAL": {
                    categoryLocation = 3;
                    break;
                }
                case "OTHER": {
                    categoryLocation = 4;
                    break;
                }
            }
            spinner.setSelection(categoryLocation);
        } /* until here - just when updating task */

        /* code for both create and update tasks */
                taskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText taskName = (EditText) findViewById(R.id.task_name_field);
                        String newTaskName = taskName.getText().toString();

                        AutoCompleteTextView memberNameStr = (AutoCompleteTextView) findViewById(R.id.task_member_field);
                        String newMemberName = memberNameStr.getText().toString();

                        EditText taskLocation = (EditText) findViewById(R.id.location_field);
                        String newTaskLocation = taskLocation.getText().toString();
                        EditText dueTime = (EditText) findViewById(R.id.due_time_field);
                        String newDueTime = dueTime.getText().toString();

                        Spinner category = (Spinner) findViewById(R.id.categorySpinner);
                        String newCategory = category.getSelectedItem().toString();

                        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.priority_group);
                        int radioButtonInt = radioGroup.getCheckedRadioButtonId();
                        RadioButton priority = (RadioButton) findViewById(radioButtonInt);
                        String newPriority = priority.getText().toString();

                        /* if one of the fields is null */
                        if ((newTaskName.equals("")) || (newMemberName.equals("")) ||
                                (newTaskLocation.equals("")) || (newDueTime.equals(""))) {
                            Toast.makeText(getApplicationContext(),
                                    "Please fill all the fields", Toast.LENGTH_SHORT).show();
                        } else { /* data is full and we can send it back */
                            Intent data = new Intent();
                            data.putExtra(Constants.TASK_NAME_TEXT, newTaskName);
                            data.putExtra(Constants.MEMBER_NAME_TEXT, newMemberName);
                            data.putExtra(Constants.TASK_LOCATION_TEXT, newTaskLocation);
                            data.putExtra(Constants.DUE_TIME_TEXT, newDueTime);
                            data.putExtra(Constants.CATEGORY_SPINNER, newCategory);
                            data.putExtra(Constants.PRIORITY_RADIO, newPriority);
                            data.putExtra("taskId", taskId);

                            setResult(Constants.RESULT_OK, data);
                            finish();
                        }
                    }
                });

    }




    /* hide the keyboard when user touch the screen */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }










}












