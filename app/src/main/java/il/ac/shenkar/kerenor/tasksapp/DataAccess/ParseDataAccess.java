package il.ac.shenkar.kerenor.tasksapp.DataAccess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

/**
 * ParseDataAccess.java - a class that does the task items operations and user operations -
 * access Parse.com
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class ParseDataAccess implements IParseDataAccess {
    private static ParseDataAccess instance;
    private Context context;


    private ParseDataAccess(Context context) {
        this.context = context;
    }


    // singleton
    public static ParseDataAccess getInstance(Context context) {
        if (instance == null)
            instance = new ParseDataAccess(context);
        return instance;
    }


    public void initializeParse() {
        Parse.initialize(context, "PfavbNiJv98cnKdf5bOo1ZVGekF0eY8TbDCpwihL",
                "ZTnhcjWwsiLvPwQB2KCr5TU0bJ23iVwI616PVRHC"); // Parse details
    }


    @Override
    public boolean checkCurrentUser(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) return true;
        else return false;
    }



    @Override
    public boolean checkIsManager(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (boolean) currentUser.get("isManager");
    }


    @Override
    public void logInToApp(String username, String password, LogInCallback logInCallback){
        ParseUser.logInInBackground(username, password, logInCallback);
    }


    @Override
    public void signUpToAppManager(String username, String password, String phone_number, SignUpCallback signUpCallback) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username);
        user.put("phone", phone_number);
        user.put("isManager", true);
        user.put("isActivated", false);

        user.signUpInBackground(signUpCallback);
    }


    @Override
    public boolean checkIfActivated(){ // check if this employee is new
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (boolean) currentUser.get("isActivated");
    }


    @Override
    public String getTeamName() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (String) currentUser.get("team");
    }


    @Override
    public void activatedAccount()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("isActivated", true);

        currentUser.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("parseDAO", "set account to activate");
                } else {
                    Log.e("parseDAO", "couldn't set account to activate" + e.getMessage());
                }
            }
        });
    }


    @Override
    public void addTaskToParse(int newTaskId, String taskNameText, TaskStatus taskStatus, TaskPriority priority,
        TaskAccept taskAccept, TaskCategory category, String memberNameText, String dueTimeText, String taskLocationText)
    {
        ParseObject taskObject = new ParseObject("Task");
        taskObject.put("taskName", taskNameText);
        taskObject.put("taskPriority", priority.toString());
        taskObject.put("taskCategory", category.toString());
        taskObject.put("dueTime", dueTimeText);
        taskObject.put("location", taskLocationText);
        taskObject.put("memberName", memberNameText);
        taskObject.put("accept", taskAccept.toString());
        taskObject.put("status", taskStatus.toString());
        taskObject.put("taskIdSql", newTaskId);
        taskObject.put("lastUpdatedByManager", Constants.LAST_UPDATED_BY_MANAGER);
        taskObject.put("needToDownload", Constants.NEED_TO_DOWNLOAD);
        taskObject.put("team", ParseUser.getCurrentUser().get("team").toString());

        taskObject.saveEventually();
    }


    @Override
    public void updateTaskParseManager(int taskId, String taskNameText, TaskPriority priority,
                                       TaskCategory category, String memberNameText, String dueTimeText, String taskLocationText)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");

        final String taskNameStr = taskNameText;
        final String priorityStr = priority.toString();
        final String categoryStr = category.toString();
        final String dueTimeStr = dueTimeText;
        final String taskLocationStr = taskLocationText;

        query.whereEqualTo("taskIdSql", taskId);
        query.whereEqualTo("memberName", memberNameText);

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject task, ParseException e) {

                if (e == null) {
                    //    Log.i("ParseDAO", "found task in parse");

                    task.put("taskName", taskNameStr);
                    task.put("taskPriority", priorityStr);
                    task.put("taskCategory", categoryStr);
                    task.put("dueTime", dueTimeStr);
                    task.put("location", taskLocationStr);
                    task.put("lastUpdatedByManager", Constants.LAST_UPDATED_BY_MANAGER);
                    task.put("needToDownload", Constants.NEED_TO_DOWNLOAD);

                    task.saveEventually();

                } else {
                    Log.e("ParseDAO", "task wasn't found  in parse !!!");
                }
            }
        });
    }


    @Override
    public void updateTaskParseEmployee(int taskId, String memberEmail, TaskStatus status, TaskAccept accept, String picturePath)
    {
        final String statusStr = status.toString();
        final String acceptStr = accept.toString();
        final String picturePathStr = picturePath;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("taskIdSql", taskId);
        query.whereEqualTo("memberName", memberEmail);

        query.getFirstInBackground(new GetCallback<ParseObject>() { // Retrieve the object by id
            @Override
            public void done(ParseObject task, ParseException e) {
                if (e == null) {
                    task.put("status", statusStr);
                    task.put("accept", acceptStr);
                    task.put("lastUpdatedByManager", Constants.LAST_UPDATED_BY_EMPLOYEE);
                    task.put("needToDownload", Constants.NEED_TO_DOWNLOAD);

                    if (picturePathStr != null) {
                        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + picturePathStr);
                        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/2, bmp.getHeight() /2, false); // set picture size to half
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                        byte[] data = stream.toByteArray();

                        try {
                            ParseFile taskImageFile = new ParseFile(picturePathStr, data);
                            taskImageFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e("parseDAO", "error saving picture: " + e.getMessage());
                                        Toast.makeText(context, "Error saving task status: Please try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.i("parseDAO", "Picture saved successfully");
                                        Toast.makeText(context, "Picture saved successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            task.put("taskImage", taskImageFile);
                        }
                        catch (Exception exception) {
                            Log.e("ParseDAO Exception", "Error saving picture to parse" + exception.getMessage());
                        }
                    }
                    task.saveInBackground(); // save the task, with or without the image
                } else {
                    Log.e("ParseDAO", "task wasn't found in parse !");
                    Toast.makeText(context, "Error saving task status: Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void getMembersFromParse(String teamName, FindCallback findCallback)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("team", teamName);
        query.whereEqualTo("isManager", false);
        query.findInBackground(findCallback);
    }



    @Override
    public void syncParseSqlManager(String teamName, FindCallback findCallback)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("team", teamName);
        query.whereEqualTo("needToDownload", Constants.NEED_TO_DOWNLOAD);
        query.whereEqualTo("lastUpdatedByManager", Constants.LAST_UPDATED_BY_EMPLOYEE);
        query.findInBackground(findCallback);
    }



    @Override
    public void syncParseSqlEmployee(String eMail, FindCallback findCallback)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("memberName", eMail);
        query.whereEqualTo("needToDownload", Constants.NEED_TO_DOWNLOAD);
        query.whereEqualTo("lastUpdatedByManager", Constants.LAST_UPDATED_BY_MANAGER);
        query.findInBackground(findCallback);
    }



    @Override
    public void setManagerTeamName(String teamName)
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("team", teamName);
        currentUser.put("isActivated", true);
        currentUser.saveEventually();
    }



    @Override
    public void signMemberToApp(String username, String password, String phone_number,
                                String teamName, SignUpCallback signUpCallback)
    {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username);
        user.put("phone", phone_number);
        user.put("isManager", false);
        user.put("isActivated", false);
        user.put("team", teamName);

        user.signUpInBackground(signUpCallback);
    }


    @Override
    public void getAllTasksByTeamFromParse(String teamName, FindCallback findCallback)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("team", teamName);
        query.findInBackground(findCallback);
    }


    @Override
    public void getAllTasksByEMailFromParse(String eMail, FindCallback findCallback)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("memberName", eMail);
        query.findInBackground(findCallback);
    }


    @Override
    public void checkIfTaskDone(int taskId, GetCallback getCallback)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereEqualTo("taskIdSql", taskId);
        query.whereEqualTo("status", "DONE");

        query.getFirstInBackground(getCallback);

    }


    @Override
    public void getImageFromParse(ParseFile taskImageParse, GetDataCallback getDataCallback) {
        taskImageParse.getDataInBackground(getDataCallback);
    }






}







