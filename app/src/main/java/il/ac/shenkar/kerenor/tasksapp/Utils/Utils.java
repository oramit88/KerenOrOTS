package il.ac.shenkar.kerenor.tasksapp.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import il.ac.shenkar.kerenor.tasksapp.Activities.TabLayoutActivity;
import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.Controllers.TaskController;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import il.ac.shenkar.kerenor.tasksapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils.java - a class that contain useful static functions to use from any class in this app
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class Utils {

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static boolean isDeviceConnectedToTheInternet(Context ctx) {
        NetworkInfo networkInfo =
                ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    /* function that convert list of ParseObject to a list of TaskItem */
    public static List<TaskItem> fromParseListToTasksList(List<ParseObject> parseList)
    {
        List<TaskItem> tasksList = new ArrayList<>();

        for (ParseObject obj : parseList) {
            String location_with_star = "*" + obj.get("location").toString(); // to highlight the new row

            TaskItem taskItem = new TaskItem(Integer.parseInt(obj.get("taskIdSql").toString()),
                    obj.get("taskName").toString(),
                    TaskStatus.valueOf(obj.get("status").toString()),
                    TaskPriority.valueOf(obj.get("taskPriority").toString()),
                    TaskAccept.valueOf(obj.get("accept").toString()),
                    TaskCategory.valueOf(obj.get("taskCategory").toString()),
                    obj.get("memberName").toString(),
                    obj.get("dueTime").toString(),
                    location_with_star);
            tasksList.add(taskItem);
        }
        return tasksList;
    }


    /* function that opens an intent and send notification to user's mobile */
    public static void sendNotificationToUserDevice(Context context)
    {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, TabLayoutActivity.class), 0);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_push_notification)
                .setContentTitle("KerenOr OTS Notification")
                .setContentText("new update arrived")
                .setContentIntent(resultPendingIntent).build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        // notification sound
        Uri uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, uriNotification);
        r.play();

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);

    }


    /* function that check if there is at least one updated task in parse by team name */
    public static void checkUpdatesForManager(ParseController parseController, TaskController taskController,
                                              Context context, String teamName)
    {
        final Context newContext = context;
        final TaskController newTaskController = taskController;

        parseController.syncParseSqlManager(teamName, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                // got the list of team tasks that employee updated and weren't downloaded
                if (e == null) {
                    Log.e("utils","list size: " + objects.size());

                    if (objects.size() > 0) {
                        Utils.sendNotificationToUserDevice(newContext); // send notification to manager

                        List<TaskItem> tasksList = Utils.fromParseListToTasksList(objects);
                        newTaskController.updateSqlDBFromParseToManager(tasksList); // send all list to SQLite
                        newTaskController.invokeDataSourceChanged();

                        for (ParseObject task : objects) {
                            task.put("needToDownload", Constants.ALREADY_DOWNLOAD);
                            task.saveEventually();
                        }
                    }
                }
                else {
                    Log.e("Util", "manager error sync: " + e.getMessage());
                }
            }
        });


    }


    /* function that check if there is at least one updated task in parse by member's eMail*/
    public static void checkUpdatesForEmployee(ParseController parseController, TaskController taskController,
                                               Context context, String eMail)
    {
        final Context newContext = context;
        final TaskController newTaskController = taskController;

        parseController.syncParseSqlEmployee(eMail, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("utils","list size: " + objects.size());

                    if (objects.size() > 0) {
                        Utils.sendNotificationToUserDevice(newContext); // send notification to employee

                        List<TaskItem> tasksList = Utils.fromParseListToTasksList(objects);
                        newTaskController.updateSqlDBFromParseToEmployee(tasksList); // send all list to SQLite
                        newTaskController.invokeDataSourceChanged();

                        for (ParseObject task : objects) {
                            task.put("needToDownload", Constants.ALREADY_DOWNLOAD);
                            task.saveEventually();
                        }
                    }
                } else {
                    Log.e("Util", "employee error sync: " + e.getMessage());
                }
            }
        });


    }








}
