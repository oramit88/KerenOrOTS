package il.ac.shenkar.kerenor.tasksapp.SqlDatabase;

import android.provider.BaseColumns;

/**
 * TasksDatabaseStructure.java - a class that define the SQLite local DB structure
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TasksDatabaseStructure {

    /* Inner class that defines the table contents of the tasks */
    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_NAME = "task_name";
        public static final String COLUMN_TASK_STATUS = "task_status";
        public static final String COLUMN_TASK_PRIORITY = "task_priority";
        public static final String COLUMN_TASK_ACCEPT = "task_accept";
        public static final String COLUMN_TASK_CATEGORY = "task_category";
        public static final String COLUMN_TEAM_MEMBER = "member_name";
        public static final String COLUMN_TASK_DUE_DATE = "task_due_date";
        public static final String COLUMN_TASK_LOCATION= "task_location";
    }


}
