package il.ac.shenkar.kerenor.tasksapp.SqlDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;

/**
 * TasksDatabaseStructure.java - a class that help us do basic operations in SQLite local DB
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TasksDatabaseHelper extends SQLiteOpenHelper {

    public TasksDatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.SQL_DELETE_TABLE);
        onCreate(db);
    }


    public void initializeSqlDB(SQLiteDatabase db) {
        db.execSQL(Constants.SQL_DELETE_TABLE);
        onCreate(db);
    }


    // print all the database
    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("DbHelper", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";
            } while (allRows.moveToNext());
        }
        return tableString;
    }












}









