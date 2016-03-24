package il.ac.shenkar.kerenor.tasksapp.Controllers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import il.ac.shenkar.kerenor.tasksapp.DataAccess.DataAccess;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.IDataAccess;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import il.ac.shenkar.kerenor.tasksapp.Listeners.OnDataSourceChangeListener;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;

/**
 * TaskController.java - a class that control the task items operations
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TaskController {
    private IDataAccess dataAccess;
    private Context context;

    //observers list.
    private List<OnDataSourceChangeListener> dataSourceChangedListeners = new ArrayList<>();


    public TaskController(Context context) {
        dataAccess = DataAccess.getInstance(context.getApplicationContext());
    }

    public List<TaskItem> getAllTasks() {
        try {
            return dataAccess.getAllTasks();
        }
        catch (Exception e) {
            return new ArrayList<>(); /* in case of error, return empty list */
        }
    }


    public List<TaskItem> getWaitingTasks() {
        try {
            List<TaskItem> tempTasksList = dataAccess.getAllTasks();
            List<TaskItem> tasksList = new ArrayList<>();

            for (TaskItem task : tempTasksList) {
                if (task.getTaskStatus().toString().equals("WAITING")) {
                    tasksList.add(task);
                }
            }
            return tasksList;
        }
        catch (Exception e) {
            return new ArrayList<>(); /* in case of error, return empty list */
        }
    }


    public List<TaskItem> getAllTasksByStatus() {
        try {
            List<TaskItem> tasksList = dataAccess.getAllTasks();

            Collections.sort(tasksList, new Comparator<TaskItem>() {
                @Override
                public int compare(TaskItem lhs, TaskItem rhs) {
                    return lhs.getTaskStatus().toString().compareToIgnoreCase(rhs.getTaskStatus().toString());
                }
            });
            return tasksList;
        }
        catch (Exception e) {
            return null;
        }
    }



    public List<TaskItem> getAllTasksByPriority() {
        try {
            List<TaskItem> tasksList = dataAccess.getAllTasks();

            Collections.sort(tasksList, new Comparator<TaskItem>() {
                @Override
                public int compare(TaskItem lhs, TaskItem rhs) {
                    return lhs.getTaskPriority().toString().compareToIgnoreCase(rhs.getTaskPriority().toString());
                }
            });
            return tasksList;
        }
        catch (Exception e) {
            return null;
        }
    }




    public int addTask(TaskItem item) {
        try {
            TaskItem retTask = dataAccess.addTask(item);
            if(retTask == null) return 0;
            invokeDataSourceChanged();
            return retTask.getTaskId();
        } catch (Exception e) {
            Log.e("TaskControl addParse", e.getMessage());
            return 0;
        }
    }



    public void updateTaskSqlManager(int taskId, String taskNameText, TaskPriority priority,
                                     TaskCategory category, String memberNameText, String dueTimeText, String taskLocationText)
     {
        try {
            dataAccess.updateTaskSqlManager(taskId, taskNameText, priority, category, memberNameText, dueTimeText, taskLocationText);
            invokeDataSourceChanged();
        } catch (Exception e) {
            Log.e("TaskController update", e.getMessage());
        }
    }


    public void updateTaskSqlEmployee(int taskId, TaskStatus status, TaskAccept accept, String location)
    {
        dataAccess.updateTaskSqlEmployee(taskId, status, accept, location);
    }


    public void registerOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
       if(listener!=null)
         dataSourceChangedListeners.add(listener);
    }


    public void unRegisterOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
       if(listener != null)
           dataSourceChangedListeners.remove(listener);
    }


    public void invokeDataSourceChanged()
    {
        for (OnDataSourceChangeListener listener : dataSourceChangedListeners) {
            listener.dataSourceChanged();
        }
    }


    public void SetTaskAsDone(int itemId)
    {
        dataAccess.SetAsDone(itemId); // check if change was made successfully
        invokeDataSourceChanged();
    }


    public void updateSqlDBFromParseToManager(List<TaskItem> tasksList)
    {
        dataAccess.updateSqlDBFromParse(tasksList);
    }


    public void updateSqlDBFromParseToEmployee(List<TaskItem> tasksList) {
        dataAccess.updateSqlDBFromParse(tasksList);
    }



    public boolean checkIfSqlEmpty () {
        return dataAccess.checkIfSqlEmpty();
    }



    public void deleteSqlDB()
    {
        dataAccess.deleteSqlDB();
    }



    public void overrideSqlDB(List<TaskItem> tasksList)
    {
        dataAccess.overrideSqlDB(tasksList);
    }








}
