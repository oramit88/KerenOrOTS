package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import il.ac.shenkar.kerenor.tasksapp.Adapters.ViewPagerAdapter;
import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.Controllers.TaskController;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import il.ac.shenkar.kerenor.tasksapp.Listeners.OnDataSourceChangeListener;
import il.ac.shenkar.kerenor.tasksapp.Utils.AlarmReceiver;
import il.ac.shenkar.kerenor.tasksapp.Utils.AnalyticsApplication;
import il.ac.shenkar.kerenor.tasksapp.Utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseUser;

/**
 * TabLayoutActivity.java - a class that control the two tabs (fragments) and the drawer menu
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TabLayoutActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnDataSourceChangeListener {

    ViewPagerAdapter pageAdapter;
    ViewPager viewPager;
    TaskController controller;
    ParseController parseController;

    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent alarmIntent;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(il.ac.shenkar.kerenor.tasksapp.R.layout.activity_homepage);

        /* google analytics code */
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();

        Log.i("tabs", "connected as: " + ParseUser.getCurrentUser().getUsername());

        /* take the service details from SharedPreferences */
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
        int notification_interval = sharedPreferences.getInt("notification_interval", -1);

        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putString("isManager", ParseUser.getCurrentUser().get("isManager").toString());
        editor1.putString("teamName", ParseUser.getCurrentUser().get("team").toString());
        editor1.apply(); // apply instead of commit, do it in background

        /* set the default notification interval to 5 minutes, if another value isn't existing */
        if (notification_interval == -1) {
            notification_interval = 5;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("notification_interval", notification_interval);
            editor.apply();
        }

        /* Retrieve a PendingIntent that will perform a broadcast */
        alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(TabLayoutActivity.this, Constants.PENDING_INTENT, alarmIntent, 0);
        alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                notification_interval * 60000, pendingIntent);
        Log.i("tabs", "created alarm");

        /* create two tabs and set theirs options */
        TabLayout tabLayout = (TabLayout) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Waiting Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("All Tasks"));

        controller = new TaskController(this);
        parseController = new ParseController(this);
        controller.registerOnDataSourceChanged(this);

        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager = (ViewPager) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.pager);
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                dataSourceChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                dataSourceChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.BROADCAST_EDIT_FROM_FRAGMENT_TO_ACTIVITY));

        boolean isManager = (boolean) ParseUser.getCurrentUser().get("isManager");
        final FloatingActionButton addNewTaskButton = (FloatingActionButton) this.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.AddTaskFloatingButton);

        NavigationView navigationView = (NavigationView) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView current_user_email_hello = (TextView) header.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.current_user_email_hello_field);
        current_user_email_hello.setText(ParseUser.getCurrentUser().getUsername());

        if (isManager) { /* manager */
            /* google analytics code */
            mTracker.setScreenName("Manager homepage screen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            addNewTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   // "ADD NEW TASK" button
                    Intent addTaskIntent = new Intent(v.getContext(), CreateEditTaskActivity.class);
                    addTaskIntent.putExtra("requestCode", Constants.CREATE_TASK);
                    startActivityForResult(addTaskIntent, Constants.CREATE_TASK);
                }
            });
        } else { /* employee */
            /* google analytics code */
            mTracker.setScreenName("User homepage screen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            /* hide the "add task" floating button */
            addNewTaskButton.setVisibility(View.GONE);

            /* hide the "manage team" from the drawer menu */
            Menu menu = navigationView.getMenu();
            menu.getItem(0).setVisible(false);
        }

        /* set the "refresh" floating button */
        FloatingActionButton refreshButton = (FloatingActionButton) this.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.refreshFloatingButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parseController.checkIsManager()) // manager
                {
                    Log.e("refresh button", "bring manager data from parse");
                    String teamName = ParseUser.getCurrentUser().get("team").toString();
                    Utils.checkUpdatesForManager(parseController, controller, getApplicationContext(), teamName);
                } else { // employee
                    Log.e("refresh button", "bring employee data from parse");
                    String eMail = ParseUser.getCurrentUser().get("email").toString();
                    Utils.checkUpdatesForEmployee(parseController, controller, getApplicationContext(), eMail);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.toolbar_tabs);
        toolbar.setTitle("");

        Drawable drawableIcon = ContextCompat.getDrawable(getApplicationContext(), il.ac.shenkar.kerenor.tasksapp.R.drawable.ic_sort);
        toolbar.setOverflowIcon(drawableIcon);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, il.ac.shenkar.kerenor.tasksapp.R.string.navigation_drawer_open, il.ac.shenkar.kerenor.tasksapp.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /* change the headline font */
        Typeface chalkFont = Typeface.createFromAsset(this.getAssets(), "fonts/DK Cool Crayon.ttf");
        CoordinatorLayout drawer_layout_toolbar = (CoordinatorLayout) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.drawer_layout_toolbar);
        TextView toolbar_title = (TextView) drawer_layout_toolbar.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.toolbar_title);
        toolbar_title.setTypeface(chalkFont);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(il.ac.shenkar.kerenor.tasksapp.R.menu.menu_sort, menu);

        return true;
    }



    /* handle action bar item clicks here (sort) */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == il.ac.shenkar.kerenor.tasksapp.R.id.sortByDueTime) {
            Log.e("tabs", "sortByDueTime");
            Intent intent = new Intent(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT_SORT);
            intent.putExtra("sort", "byTime");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return true;
        }
        else if (id == il.ac.shenkar.kerenor.tasksapp.R.id.sortByStatus) {
            Log.e("tabs", "sortByStatus");
            Intent intent = new Intent(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT_SORT);
            intent.putExtra("sort", "byStatus");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return true;
        }
        else if (id == il.ac.shenkar.kerenor.tasksapp.R.id.sortByPriority) {
            Log.e("tabs", "sortByPriority");
            Intent intent = new Intent(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT_SORT);
            intent.putExtra("sort", "byPriority");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /* handle navigation view item clicks here (drawer left menu) */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == il.ac.shenkar.kerenor.tasksapp.R.id.manage_team) {
            Intent intent = new Intent(this, CreateEditTeamActivity.class);
            boolean isActivated = (boolean)ParseUser.getCurrentUser().get("isActivated");
            if (isActivated) {
                intent.putExtra("requestCode", Constants.UPDATE_TEAM);
            } else {
                intent.putExtra("requestCode", Constants.CREATE_TEAM);
            }
            startActivity(intent);
            finish();

        } else if (id == il.ac.shenkar.kerenor.tasksapp.R.id.notification_interval) {
            chooseInterval();

        } else if (id == il.ac.shenkar.kerenor.tasksapp.R.id.about) {
            showAbout();

        } else if (id == il.ac.shenkar.kerenor.tasksapp.R.id.sign_out) {
            sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, 0);
            sharedPreferences.edit().clear().apply(); // delete all the shared preferences
            controller.deleteSqlDB();
            ParseUser.logOut();

            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
            Log.i("tabs", "canceled alarm");

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /* intents that come back from create task or update task */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Constants.CREATE_TASK) {

                Bundle extras = data.getExtras();
                if (extras != null) {
                    String taskNameText = data.getStringExtra(Constants.TASK_NAME_TEXT);
                    String memberNameText = data.getStringExtra(Constants.MEMBER_NAME_TEXT);
                    String taskLocationText = data.getStringExtra(Constants.TASK_LOCATION_TEXT);
                    String dueTimeText = data.getStringExtra(Constants.DUE_TIME_TEXT);
                    String categoryText = data.getStringExtra(Constants.CATEGORY_SPINNER);
                    String priorityText = data.getStringExtra(Constants.PRIORITY_RADIO);

                    // string to enum
                    TaskCategory category = TaskCategory.valueOf(categoryText);
                    TaskPriority priority = TaskPriority.valueOf(priorityText.toUpperCase());

                    // add task to sql
                    int newTaskId = controller.addTask(new TaskItem(taskNameText, TaskStatus.WAITING, priority,
                            TaskAccept.WAITING, category, memberNameText, dueTimeText, taskLocationText));

                    // add task to parse
                    parseController.addTaskToParse(newTaskId, taskNameText, TaskStatus.WAITING, priority,
                            TaskAccept.WAITING, category, memberNameText, dueTimeText, taskLocationText);
                }
            }
            else if (requestCode == Constants.UPDATE_TASK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    boolean isManager = (boolean) ParseUser.getCurrentUser().get("isManager");

                    if (isManager) {
                        String taskNameText = data.getStringExtra(Constants.TASK_NAME_TEXT);
                        String memberNameText = data.getStringExtra(Constants.MEMBER_NAME_TEXT);
                        String taskLocationText = data.getStringExtra(Constants.TASK_LOCATION_TEXT);
                        String dueTimeText = data.getStringExtra(Constants.DUE_TIME_TEXT);
                        String categoryText = data.getStringExtra(Constants.CATEGORY_SPINNER);
                        String priorityText = data.getStringExtra(Constants.PRIORITY_RADIO);
                        int taskId = data.getExtras().getInt("taskId");

                        // string to enum
                        TaskCategory category = TaskCategory.valueOf(categoryText);
                        TaskPriority priority = TaskPriority.valueOf(priorityText.toUpperCase());

                        // update task in sql
                        controller.updateTaskSqlManager(taskId, taskNameText, priority,
                                category, memberNameText, dueTimeText, taskLocationText);

                        // update task in parse
                        parseController.updateTaskParseManager(taskId, taskNameText, priority,
                                category, memberNameText, dueTimeText, taskLocationText);
                    } else { // report from employee
                        String memberEmail = ParseUser.getCurrentUser().getEmail();

                        int taskId = data.getExtras().getInt("taskId");
                        String statusText = data.getStringExtra(Constants.STATUS_RADIO);
                        String acceptText = data.getStringExtra(Constants.ACCEPT_RADIO);
                        String picturePath = data.getStringExtra(Constants.PICTURE);
                        String taskLocationText = data.getStringExtra(Constants.TASK_LOCATION_TEXT);

                        // string to enum
                        TaskStatus status = TaskStatus.valueOf(statusText.toUpperCase());
                        TaskAccept accept = TaskAccept.valueOf(acceptText.toUpperCase());

                        // update task in sql
                        controller.updateTaskSqlEmployee(taskId, status, accept, taskLocationText);

                        // update task in parse
                        parseController.updateTaskParseEmployee(taskId, memberEmail, status, accept, picturePath);
                        dataSourceChanged();
                    }
                }
            }
        }
    }



    @Override
    public void dataSourceChanged() {
        Intent intent = new Intent(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent fragmentIntent) {
            boolean isManager = (boolean)(ParseUser.getCurrentUser().get("isManager"));
            if (isManager) {
                fragmentIntent.setClass(getApplicationContext(), CreateEditTaskActivity.class);
            } else {
                fragmentIntent.setClass(getApplicationContext(), ReportTaskEmployeeActivity.class);
            }
            startActivityForResult(fragmentIntent, Constants.UPDATE_TASK);
        }
    };





    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }



    /* Inflate the 'about' dialog contents */
    protected void showAbout() {
        View messageView = getLayoutInflater().inflate(il.ac.shenkar.kerenor.tasksapp.R.layout.dialog_about, null, false);

        TextView textView = (TextView) messageView.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText(il.ac.shenkar.kerenor.tasksapp.R.string.app_name);
        title.setPadding(10, 30, 10, 10); // left, up, right, down
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        builder.setCustomTitle(title);
        title.setTextColor(Color.parseColor("#faf5e4"));
        title.setBackgroundColor(Color.parseColor("#009688"));

        // change the headline font
        Typeface chalkFont = Typeface.createFromAsset(this.getAssets(), "fonts/DK Cool Crayon.ttf");
        title.setTypeface(chalkFont);

        builder.setView(messageView);
        builder.create();
        builder.show();
    }



    /* Inflate the 'choose interval' dialog contents */
    public void chooseInterval() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(new ContextThemeWrapper(this, il.ac.shenkar.kerenor.tasksapp.R.style.AlertDialogCustom));
        builderSingle.setIcon(il.ac.shenkar.kerenor.tasksapp.R.drawable.icon_notification);
        builderSingle.setTitle("Set Notification Interval");

        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
        int notification_interval = sharedPreferences.getInt("notification_interval", -1);
        int chosen_item = -1;

        switch (notification_interval) {
            case 0: {
                chosen_item = 0;
                break;
            }
            case 1: {
                chosen_item = 1;
                break;
            }
            case 5: {
                chosen_item = 2;
                break;
            }
            case 10: {
                chosen_item = 3;
                break;
            }
            case 30: {
                chosen_item = 4;
                break;
            }
        }

        final int chosen_item1 = chosen_item;

        ListView intervalList = new ListView(this);
        String[] choices = {"no notification", "1 minute", "5 minutes (default)", "10 minute", "30 minute"};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, il.ac.shenkar.kerenor.tasksapp.R.layout.array_adapter_item, choices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(il.ac.shenkar.kerenor.tasksapp.R.id.interval_item);

                text.setBackgroundColor(Color.WHITE);

                if (chosen_item1 == position) {
                    text.setBackgroundColor(Color.parseColor("#ebd2f6"));
                }
                return view;
            }
        };
        intervalList.setAdapter(arrayAdapter);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int notification_interval = sharedPreferences.getInt("notification_interval", 5);

                switch (which) {
                    case 0: {
                        editor.putInt("notification_interval", 0);
                        notification_interval = 0;
                        alarmManager.cancel(pendingIntent);
                        break;
                    }
                    case 1: {
                        editor.putInt("notification_interval", 1);
                        notification_interval = 1;
                        break;
                    }
                    case 2: {
                        editor.putInt("notification_interval", 5);
                        notification_interval = 5;
                        break;
                    }
                    case 3: {
                        editor.putInt("notification_interval", 10);
                        notification_interval = 10;
                        break;
                    }
                    case 4: {
                        editor.putInt("notification_interval", 30);
                        notification_interval = 30;
                        break;
                    }
                }

                if (notification_interval != 0) {
                    alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                            notification_interval * 60000, pendingIntent);
                }

                editor.apply();
            }
        });
        builderSingle.show();
    }









}
