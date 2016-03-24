package il.ac.shenkar.kerenor.tasksapp.Controllers;

import android.content.Context;

import il.ac.shenkar.kerenor.tasksapp.DataAccess.IParseDataAccess;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.ParseDataAccess;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseFile;
import com.parse.SignUpCallback;

/**
 * ParseController.java - a class that control the parse.com operations
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class ParseController {
    private IParseDataAccess parseDataAccess;
    private Context context;

    public ParseController(Context context) {
        parseDataAccess = ParseDataAccess.getInstance(context.getApplicationContext());
    }

    /* users management methods (parse) */
    public void initializeParseDB() {
        parseDataAccess.initializeParse();
    }

    public boolean checkCurrentUser() {
        return parseDataAccess.checkCurrentUser();
    }

    public boolean checkIsManager() {
        return parseDataAccess.checkIsManager();
    }

    public void logInToApp(String username, String password, LogInCallback logInCallback) {
        parseDataAccess.logInToApp(username, password, logInCallback);
    }

    public void signUpToAppManager(String username, String password, String phone_number, SignUpCallback signUpCallback) {
        parseDataAccess.signUpToAppManager(username, password, phone_number, signUpCallback);
    }

    public boolean checkIfActivated() {
        return parseDataAccess.checkIfActivated();
    }

    public String getTeamName() {
        return parseDataAccess.getTeamName();
    }

    public void activatedAccount() { parseDataAccess.activatedAccount(); }

    /* tasks management methods (parse) */
    public void addTaskToParse(int newTaskId, String taskNameText, TaskStatus taskStatus,
                               TaskPriority priority, TaskAccept taskAccept, TaskCategory category,
                               String memberNameText, String dueTimeText, String taskLocationText)
    {
        parseDataAccess.addTaskToParse(newTaskId, taskNameText, taskStatus, priority, taskAccept, category,
                memberNameText, dueTimeText, taskLocationText);
    }

    public void updateTaskParseManager(int taskId, String taskNameText, TaskPriority priority,
                                       TaskCategory category, String memberNameText,
                                       String dueTimeText, String taskLocationText)
    {
        parseDataAccess.updateTaskParseManager(taskId, taskNameText, priority,
                category, memberNameText, dueTimeText, taskLocationText);
    }


    public void updateTaskParseEmployee(int taskId, String memberEmail, TaskStatus status,
                                        TaskAccept accept, String picturePath)
    {
        parseDataAccess.updateTaskParseEmployee(taskId, memberEmail, status, accept, picturePath);
    }



    public void signMemberToApp(String email, String password, String phone_number, String teamName, SignUpCallback signUpCallback){
        parseDataAccess.signMemberToApp(email, password, phone_number, teamName, signUpCallback);
    }


     public void getMembersFromParse(String teamName, FindCallback findCallback)
     {
         parseDataAccess.getMembersFromParse(teamName, findCallback);
     }



    public void syncParseSqlManager(String teamName, FindCallback findCallback)
    {
        parseDataAccess.syncParseSqlManager(teamName, findCallback);
    }


    public void syncParseSqlEmployee(String eMail, FindCallback findCallback)
    {
        parseDataAccess.syncParseSqlEmployee(eMail, findCallback);
    }



    public void setManagerTeamName(String teamNameStr)
    {
        parseDataAccess.setManagerTeamName(teamNameStr);
    }



    public void getAllTasksByTeamFromParse(String teamName, FindCallback findCallback)
    {
        parseDataAccess.getAllTasksByTeamFromParse(teamName, findCallback);
    }


    public void getAllTasksByEMailFromParse(String eMail, FindCallback findCallback)
    {
        parseDataAccess.getAllTasksByEMailFromParse(eMail, findCallback);
    }

    public void checkIfTaskDone(int taskId, GetCallback getCallback) {
        parseDataAccess.checkIfTaskDone(taskId, getCallback);
    }



    public void getImageFromParse(ParseFile taskImageParse, GetDataCallback getDataCallback) {
        parseDataAccess.getImageFromParse(taskImageParse, getDataCallback);
    }








}
















