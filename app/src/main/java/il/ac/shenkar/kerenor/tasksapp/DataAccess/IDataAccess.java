package il.ac.shenkar.kerenor.tasksapp.DataAccess;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;

import java.util.List;

/**
 * IDataAccess.java - an interface that control the task items operations - access SQLite
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public interface IDataAccess {
    List<TaskItem> getAllTasks();
    TaskItem addTask(TaskItem taskItem);
    void SetAsDone(int itemId);
    boolean updateTaskSqlFromParse(int taskId, String taskNameText, TaskStatus status, TaskPriority priority, TaskAccept accept,
                                   TaskCategory category, String memberNameText, String dueTimeText, String taskLocationText);
    void updateTaskSqlManager(int taskId, String taskNameText, TaskPriority priority,
                                   TaskCategory category, String memberNameText, String dueTimeText, String taskLocationText);
    boolean updateTaskSqlEmployee(int itemId, TaskStatus status, TaskAccept accept, String location);
//    void updateSqlDBFromParseToManager(List<TaskItem> tasksList);
    void updateSqlDBFromParse(List<TaskItem> tasksList);
    boolean checkIfSqlEmpty ();
    void deleteSqlDB();
    void overrideSqlDB(List<TaskItem> tasksList);




}
