package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.R;
import il.ac.shenkar.kerenor.tasksapp.Utils.AnalyticsApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReportTaskEmployeeActivity.java - a class that let the employee report his/her tasks
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class ReportTaskEmployeeActivity extends AppCompatActivity {
    int taskId;
    String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_task_employee);

        /* google analytics code */
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Report screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.report_title);

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


        /* "add photo" button */
        Button addPhotoButton = (Button)findViewById(R.id.add_photo_button);
        addPhotoButton.setVisibility(View.INVISIBLE); /* hide button until listener find (status == done) */
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm").format(new Date());
                    File image = new File(Environment.getExternalStorageDirectory(), "PIC_" + timeStamp + ".jpg");
                    Uri uriSavedImage = Uri.fromFile(image);
                    picturePath = "PIC_" + timeStamp + ".jpg";

                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(imageIntent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            "Problem with the camera. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intentTaskDetails = getIntent();
        int requestCode = intentTaskDetails.getExtras().getInt("requestCode");

        if (requestCode == Constants.UPDATE_TASK) {
            String taskName = intentTaskDetails.getStringExtra(Constants.TASK_NAME_TEXT);
            String taskLocation = intentTaskDetails.getStringExtra(Constants.TASK_LOCATION_TEXT);
            String taskDueDate = intentTaskDetails.getStringExtra(Constants.DUE_TIME_TEXT);
            String taskCategory = intentTaskDetails.getStringExtra(Constants.CATEGORY_SPINNER);
            String taskPriority = intentTaskDetails.getStringExtra(Constants.PRIORITY_RADIO);
            String taskStatus = intentTaskDetails.getStringExtra(Constants.STATUS_RADIO);
            String taskAccept = intentTaskDetails.getStringExtra(Constants.ACCEPT_RADIO);
            taskId = intentTaskDetails.getExtras().getInt("taskId");

            TextView taskNameField = (TextView) findViewById(R.id.task_name_field);
            taskNameField.setText(taskName);
            TextView taskCategoryField = (TextView) findViewById(R.id.task_category_field);
            taskCategoryField.setText(taskCategory);
            TextView taskLocationField = (TextView) findViewById(R.id.task_location_field);
            taskLocationField.setText(taskLocation);
            TextView taskDueDateField = (TextView) findViewById(R.id.due_time_field);
            taskDueDateField.setText(taskDueDate);
            TextView taskPriorityField = (TextView) findViewById(R.id.task_priority_field);
            taskPriorityField.setText(taskPriority);

            /* accept - radio group */
            switch (taskAccept) {
                case "WAITING": {
                    RadioButton waitingRadioButton = (RadioButton) findViewById(R.id.accept_waiting_radio_button);
                    waitingRadioButton.setChecked(true);
                    break;
                }
                case "ACCEPT": {
                    RadioButton acceptRadioButton = (RadioButton) findViewById(R.id.accept_accept_radio_button);
                    acceptRadioButton.setChecked(true);
                    break;
                }
                case "REJECT": {
                    RadioButton rejectRadioButton = (RadioButton) findViewById(R.id.accept_reject_radio_button);
                    rejectRadioButton.setChecked(true);
                    break;
                }
            }

            /* status - radio group */
            switch (taskStatus) {
                case "WAITING": {
                    RadioButton waitingRadioButton = (RadioButton) findViewById(R.id.status_waiting_radio_button);
                    waitingRadioButton.setChecked(true);
                    break;
                }
                case "IN_PROGRESS": {
                    RadioButton progressRadioButton = (RadioButton) findViewById(R.id.status_progress_radio_button);
                    progressRadioButton.setChecked(true);
                    break;
                }
                case "DONE": {
                    RadioButton doneRadioButton = (RadioButton) findViewById(R.id.status_done_radio_button);
                    doneRadioButton.setChecked(true);
                    break;
                }
            }
        }


        Button reportTaskButton = (Button)findViewById(R.id.report_task_button);
        reportTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("reportTaskActivity", "report task clicked");

                /* get the answers from the two groups of radio buttons */
                RadioGroup acceptRadioGroup = (RadioGroup) findViewById(R.id.accept_group);
                int acceptRadioButtonInt = acceptRadioGroup.getCheckedRadioButtonId();
                RadioButton accept = (RadioButton) findViewById(acceptRadioButtonInt);
                String newAccept = accept.getText().toString();

                RadioGroup statusRadioGroup = (RadioGroup) findViewById(R.id.status_group);
                int statusRadioButtonInt = statusRadioGroup.getCheckedRadioButtonId();
                RadioButton status = (RadioButton) findViewById(statusRadioButtonInt);
                String newStatus = status.getText().toString();

                /* we include the location because of the notification update */
                TextView taskLocation = (TextView) findViewById(R.id.task_location_field);
                String newTaskLocation = taskLocation.getText().toString();

                /* according to the spec */
                if (newAccept.equals("Accept")) {
                    String accept_message = "Task " + newAccept + ". Task is " + newStatus;
                    Toast.makeText(getApplicationContext(), accept_message, Toast.LENGTH_SHORT).show();
                } else if (newAccept.equals("Reject")) {
                    String reject_message = "Task " + newAccept + ".";
                    Toast.makeText(getApplicationContext(), reject_message, Toast.LENGTH_SHORT).show();
                }

                if (newStatus.equals("In progress")) {
                    newStatus = "IN_PROGRESS"; /* because of the eNum value */
                }

                Intent data = new Intent();
                data.putExtra(Constants.ACCEPT_RADIO, newAccept);
                data.putExtra(Constants.STATUS_RADIO, newStatus);
                data.putExtra(Constants.TASK_LOCATION_TEXT, newTaskLocation);
                data.putExtra("taskId", taskId);

                if (picturePath != null) {
                    data.putExtra(Constants.PICTURE, picturePath);
                } else
                    picturePath = "1"; /* because the intent can't read a null value */

                setResult(Constants.RESULT_OK, data);
                finish();
            }
        }
    );


        /* listener to the "done" radio button */
        RadioGroup statusRadioGroup = (RadioGroup) findViewById(R.id.status_group);
        statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Button addPhotoButton = (Button) findViewById(R.id.add_photo_button);

                    if (checkedId == R.id.status_done_radio_button) {
                        addPhotoButton.setVisibility(View.VISIBLE); /* show 'add photo' button */
                    } else {
                        addPhotoButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        );
    }
}
