package il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts;


import il.ac.shenkar.kerenor.tasksapp.SqlDatabase.TasksDatabaseStructure;

public class Constants {

    // Database
    public static final int DATABASE_VERSION = 17;
    public static final String DATABASE_NAME = "tasks.db";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + TasksDatabaseStructure.TaskEntry.TABLE_NAME + " ("
            + TasksDatabaseStructure.TaskEntry._ID + " INTEGER PRIMARY KEY,"
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE + " TEXT NOT NULL, "
            + TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION + " TEXT NOT NULL) ";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "
            + TasksDatabaseStructure.TaskEntry.TABLE_NAME;


    // activities
    public static final String TASK_NAME_TEXT = "taskNameText";
    public static final String MEMBER_NAME_TEXT = "memberNameText";
    public static final String TASK_LOCATION_TEXT = "taskLocationText";
    public static final String DUE_TIME_TEXT = "dueTimeText";
    public static final String CATEGORY_SPINNER = "categorySpinner";
    public static final String PRIORITY_RADIO = "priorityRadioButton";
    public static final String ACCEPT_RADIO = "acceptRadioButton";
    public static final String STATUS_RADIO = "statusRadioButton";
    public static final String PICTURE = "picture";

    public static final String PREFERENCES_FILE = "preferencesFile";


    // intents
    public static final int CREATE_TASK = 1;
    public static final int UPDATE_TASK = 2;
    public static final int RESULT_OK = 3;
    public static final int CREATE_TEAM = 4;
    public static final int UPDATE_TEAM = 5;
    public static final int PENDING_INTENT = 6;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 7;
    public static final int SEND_EMAIL = 8;


    // fragments
    public static final int FRAGMENTS_NUM = 2;
    public static final String BROADCAST_EDIT_FROM_FRAGMENT_TO_ACTIVITY = "broadcast from fragment to activity";
    public static final String BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT = "update adapter fragments";
    public static final String BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT_SORT = "sort adapter fragments";


    // splash screen
    public static int SPLASH_DISPLAY_LENGTH = 3000;


    // parse and sql sync
    public static boolean LAST_UPDATED_BY_MANAGER = true;
    public static boolean LAST_UPDATED_BY_EMPLOYEE = false;
    public static boolean NEED_TO_DOWNLOAD = true;
    public static boolean ALREADY_DOWNLOAD = false;



}




