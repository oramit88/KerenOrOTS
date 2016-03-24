package il.ac.shenkar.kerenor.tasksapp.DataAccess;

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
 * IParseDataAccess.java - an interface that control the task items operations and user operations -
 * access Parse.com
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public interface IParseDataAccess {

    void initializeParse();
    void logInToApp(String username, String password, LogInCallback logInCallback);
    void signUpToAppManager(String username_str, String password_str, String phone_number_str, SignUpCallback signUpCallback);
    void activatedAccount();

    boolean checkCurrentUser();
    boolean checkIsManager();
    boolean checkIfActivated();
    String getTeamName();

    void addTaskToParse(int newTaskId, String taskNameText, TaskStatus taskStatus, TaskPriority priority,
                        TaskAccept taskAccept, TaskCategory category, String memberNameText, String dueTimeText,
                        String taskLocationText);
    void updateTaskParseManager(int taskId, String taskNameText, TaskPriority priority, TaskCategory category,
                        String memberNameText, String dueTimeText, String taskLocationText);
    void updateTaskParseEmployee(int taskId, String memberEmail, TaskStatus status, TaskAccept accept,
                        String picturePath);
    void getMembersFromParse(String teamName, FindCallback findCallback);

    void syncParseSqlManager(String teamName, FindCallback findCallback);
    void syncParseSqlEmployee(String eMail, FindCallback findCallback);

    void setManagerTeamName(String teamNameStr);

    void signMemberToApp(String email, String password, String phone_number, String teamName,
                         SignUpCallback signUpCallback);

    void getAllTasksByTeamFromParse(String teamName, FindCallback findCallback);
    void getAllTasksByEMailFromParse(String eMail, FindCallback findCallback);

    void checkIfTaskDone(int taskId, GetCallback getCallback);

    void getImageFromParse(ParseFile taskImageParse, GetDataCallback getDataCallback);



}










