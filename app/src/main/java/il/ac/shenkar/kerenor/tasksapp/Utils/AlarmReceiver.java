package il.ac.shenkar.kerenor.tasksapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.Controllers.TaskController;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import com.parse.ParseUser;

/**
 * AlarmReceiver.java - a class that check if there is a new data to download
 * (the code is executed according to the interval the user selected)
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        ParseController parseController = new ParseController(context);
        final TaskController taskController = new TaskController(context);

        try {
            parseController.initializeParseDB();
        }
        catch (Exception e){
        //    Log.e("util", "parse already connected: " + e.getMessage());
        }


        if (parseController.checkIsManager()) // manager
        {
            Log.e("alarm", "bring manager data from parse");
            String teamName = ParseUser.getCurrentUser().get("team").toString();
            Utils.checkUpdatesForManager(parseController, taskController, context, teamName);
        }

        else { // employee
            Log.e("alarm", "bring employee data from parse");
            String eMail = ParseUser.getCurrentUser().get("email").toString();
            Utils.checkUpdatesForEmployee(parseController, taskController, context, eMail);
        }

        Intent intent1 = new Intent(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

    }



}
