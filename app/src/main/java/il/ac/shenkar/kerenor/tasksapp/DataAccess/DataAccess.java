package il.ac.shenkar.kerenor.tasksapp.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import il.ac.shenkar.kerenor.tasksapp.SqlDatabase.TasksDatabaseHelper;
import il.ac.shenkar.kerenor.tasksapp.SqlDatabase.TasksDatabaseStructure;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;

/**
 * DataAccess.java - a class that does the task items operations - access SQLite
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class DataAccess implements IDataAccess {
    private static DataAccess instance;
    private Context context;
    private TasksDatabaseHelper dbHelper;

    private String[] tasksColumns = {
            TasksDatabaseStructure.TaskEntry._ID,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY,
            TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE,
            TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION };


    private DataAccess(Context context) {
        this.context = context;
        this.dbHelper = new TasksDatabaseHelper(this.context);
    }

    /* Singleton implement. */
    public static DataAccess getInstance(Context context) {
        if (instance == null)
            instance = new DataAccess(context);
        return instance;
    }


    public TaskItem addTask(TaskItem taskItem) {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();

            if (taskItem == null)
                return null;

            // build the content values
            ContentValues values = new ContentValues();
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME, taskItem.getTaskName());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS,
                    String.valueOf(taskItem.getTaskStatus().name()));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY,
                    String.valueOf(taskItem.getTaskPriority().name()));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT,
                    String.valueOf(taskItem.getTaskAccept().name()));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY,
                    String.valueOf(taskItem.getTaskCategory().name()));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER, taskItem.getMemberName());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE, taskItem.getDueDate());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, taskItem.getLocation());

            // do the insert
            long insertId = database.insert(TasksDatabaseStructure.TaskEntry.TABLE_NAME, null, values);

            // get the entity from the database - extra validation, entity was insert properly
            Cursor cursor = database.query(TasksDatabaseStructure.TaskEntry.TABLE_NAME, tasksColumns,
                    TasksDatabaseStructure.TaskEntry._ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();

            TaskItem newTask = cursorToTask(cursor);
            cursor.close();
            return newTask;

        } finally {
            if (database != null)
                database.close();
        }
    }



    private TaskItem cursorToTask(Cursor cursor) {
        String taskName = cursor.getString(cursor.getColumnIndex
                (TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME));
        String mamberName = cursor.getString(cursor.getColumnIndex
                (TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER));
        String dueTime = cursor.getString(cursor.getColumnIndex
                (TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE));
        String location = cursor.getString(cursor.getColumnIndex
                (TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION));
        TaskStatus status = TaskStatus.valueOf((cursor.getString
                (cursor.getColumnIndex(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS))));
        TaskPriority priority = TaskPriority.valueOf((cursor.getString
                (cursor.getColumnIndex(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY))));
        TaskAccept accept = TaskAccept.valueOf((cursor.getString
                (cursor.getColumnIndex(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT))));
        TaskCategory category = TaskCategory.valueOf((cursor.getString
                (cursor.getColumnIndex(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY))));

        int taskId = cursor.getInt(cursor.getColumnIndex(TasksDatabaseStructure.TaskEntry._ID));

        TaskItem taskItem = new TaskItem(taskName, status, priority, accept, category,
                mamberName,dueTime, location);
        taskItem.setTaskId(taskId);

        return taskItem;
    }



    public List<TaskItem> getAllTasks() {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();
            List<TaskItem> tasksList = new ArrayList<TaskItem>();

            //////////// PRINT DB
//            Log.i("DAO", "PRINT DB");
//            String dataToLog = dbHelper.getTableAsString
//                    (database, TasksDatabaseStructure.TaskEntry.TABLE_NAME);
//            Log.i("DAO", dataToLog);


            Cursor cursor = database.query(TasksDatabaseStructure.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                TaskItem item = cursorToTask(cursor);

                tasksList.add(item);
                cursor.moveToNext();
            }
            cursor.close(); // make sure to close the cursor

            Collections.sort(tasksList, new Comparator<TaskItem>() {
                @Override
                public int compare(TaskItem lhs, TaskItem rhs) {
                    String[] separatedFirstDate = lhs.getDueDate().split("/");
                    String firstDate = separatedFirstDate[2] + separatedFirstDate[1] + separatedFirstDate[0];

                    String[] separatedSecondDate = rhs.getDueDate().split("/");
                    String secondDate = separatedSecondDate[2] + separatedSecondDate[1] + separatedSecondDate[0];

                    return firstDate.compareToIgnoreCase(secondDate);
                }
            });
            return tasksList;
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
    }



     public void SetAsDone(int itemId)
     {
         SQLiteDatabase database = null;

         try {
             database = dbHelper.getWritableDatabase();

             // build the content values
             ContentValues values = new ContentValues();
             values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS, TaskStatus.DONE.toString());

             database.update(TasksDatabaseStructure.TaskEntry.TABLE_NAME, values,
                     TasksDatabaseStructure.TaskEntry._ID + " = " + itemId, null);
         }
         finally {
             if (database != null)
                 database.close();
         }
     }




    public boolean updateTaskSqlFromParse(int itemId, String taskNameText,  TaskStatus status, TaskPriority priority,
                                          TaskAccept accept, TaskCategory category, String memberNameText,
                                          String dueTimeText, String taskLocationText)
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getWritableDatabase();

            // build the content values
            ContentValues values = new ContentValues();
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME, taskNameText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY, priority.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY, category.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS, status.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT, accept.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER, memberNameText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE, dueTimeText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, taskLocationText);

            int rowsEffected = database.update(TasksDatabaseStructure.TaskEntry.TABLE_NAME, values,
                    TasksDatabaseStructure.TaskEntry._ID + " = " + itemId, null);

            Log.e("DAO", "rowsEffected : " + rowsEffected);

            if (rowsEffected == 0) return false;
            return true;
        }
        finally {
            if (database != null)
                database.close();
        }
    }



    public void insertTaskSqlIfNotExist(int itemId, String taskName, TaskStatus status, TaskPriority priority,
                                        TaskAccept accept, TaskCategory category, String memberName, String dueTime,
                                        String taskLocation)
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();

            // build the content values
            ContentValues values = new ContentValues();
            values.put(TasksDatabaseStructure.TaskEntry._ID, itemId);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME, taskName);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS, String.valueOf(status));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY, String.valueOf(priority));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT, String.valueOf(accept));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY, String.valueOf(category));
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER, memberName);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE, dueTime);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, taskLocation);

            // do the insert
            long insertId = database.insert(TasksDatabaseStructure.TaskEntry.TABLE_NAME, null, values);

            // get the entity from the database - extra validation, entity was insert properly
            Cursor cursor = database.query(TasksDatabaseStructure.TaskEntry.TABLE_NAME, tasksColumns,
                    TasksDatabaseStructure.TaskEntry._ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();

            TaskItem newTask = cursorToTask(cursor); // create the friend object from the cursor.
            cursor.close();
        } finally {
            if (database != null)
                database.close();
        }
    }


    // only for the update when report task
    public boolean updateTaskSqlEmployee(int itemId, TaskStatus status, TaskAccept accept, String location)
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getWritableDatabase();

            // build the content values
            ContentValues values = new ContentValues();
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS, status.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT, accept.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, location);

            int rowsEffected = database.update(TasksDatabaseStructure.TaskEntry.TABLE_NAME, values,
                    TasksDatabaseStructure.TaskEntry._ID + " = " + itemId, null);
            if (rowsEffected == 0) return false;
            return true;
        }
        finally {
            if (database != null)
                database.close();
        }
    }



    // only for the update when edit task
    public void updateTaskSqlManager(int itemId, String taskNameText, TaskPriority priority, TaskCategory category,
                                     String memberNameText, String dueTimeText, String taskLocationText)
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getWritableDatabase();

            // build the content values
            ContentValues values = new ContentValues();
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME, taskNameText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY, priority.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY, category.toString());
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER, memberNameText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE, dueTimeText);
            values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, taskLocationText);

            database.update(TasksDatabaseStructure.TaskEntry.TABLE_NAME, values,
                    TasksDatabaseStructure.TaskEntry._ID + " = " + itemId, null);
        }
        finally {
            if (database != null)
                database.close();
        }


    }




    public void updateSqlDBFromParse(List<TaskItem> tasksList)
    {
        int itemId;
        String taskName, memberName, dueTime, taskLocation;
        TaskPriority priority;
        TaskCategory category;
        TaskStatus status;
        TaskAccept accept;

        for (TaskItem task : tasksList)
        {
            itemId = task.getTaskId();
            taskName = task.getTaskName();
            memberName = task.getMemberName();
            dueTime = task.getDueDate();
            taskLocation = task.getLocation();
            priority = task.getTaskPriority();
            category = task.getTaskCategory();
            status = task.getTaskStatus();
            accept = task.getTaskAccept();

            boolean ifUpdated = updateTaskSqlFromParse(itemId, taskName, status, priority, accept, category,
                    memberName, dueTime, taskLocation);
            Log.e("DAO", "ifUpdated: " + ifUpdated);
            if (!ifUpdated) {
               insertTaskSqlIfNotExist(itemId, taskName, status, priority, accept, category, memberName,
                       dueTime, taskLocation);
            }
        }
    }




    public boolean checkIfSqlEmpty ()
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();

            Cursor cursor = database.query(TasksDatabaseStructure.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) { return false; }
            cursor.close(); // make sure to close the cursor
        }
        finally {
            if (database != null) {
                database.close();
            }
        }
        return true;
    }


    public void deleteSqlDB()
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();
            dbHelper.initializeSqlDB(database);
        }
        catch (Exception exception){
            Log.e("DAO", "delete sql db error: " + exception.getMessage());
        }
        finally {
            if (database != null)
                database.close();
        }

    }



    public void overrideSqlDB(List<TaskItem> tasksList)
    {
        SQLiteDatabase database = null;

        try {
            database = dbHelper.getReadableDatabase();
            dbHelper.initializeSqlDB(database);

            if (tasksList == null)return;

            // build the content values
            for (TaskItem taskItem : tasksList) {
                ContentValues values = new ContentValues();
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_NAME, taskItem.getTaskName());
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_STATUS,
                        String.valueOf(taskItem.getTaskStatus().name()));
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_PRIORITY,
                        String.valueOf(taskItem.getTaskPriority().name()));
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_ACCEPT,
                        String.valueOf(taskItem.getTaskAccept().name()));
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_CATEGORY,
                        String.valueOf(taskItem.getTaskCategory().name()));
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TEAM_MEMBER, taskItem.getMemberName());
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_DUE_DATE, taskItem.getDueDate());
                values.put(TasksDatabaseStructure.TaskEntry.COLUMN_TASK_LOCATION, taskItem.getLocation());
                values.put(TasksDatabaseStructure.TaskEntry._ID, taskItem.getTaskId());

                // do the insert
                long insertId = database.insert(TasksDatabaseStructure.TaskEntry.TABLE_NAME, null, values);

                Cursor cursor = database.query(TasksDatabaseStructure.TaskEntry.TABLE_NAME, tasksColumns,
                        TasksDatabaseStructure.TaskEntry._ID + " = " + insertId, null, null, null, null);

                cursor.moveToFirst();

                TaskItem newTask = cursorToTask(cursor);
                cursor.close();
            }
        }
        catch (Exception exception){
            Log.e("DAO", "exception: " + exception.getMessage());
        }
        finally {
            if (database != null)
                database.close();
        }
    }







}
