package il.ac.shenkar.kerenor.tasksapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.ac.shenkar.kerenor.tasksapp.Adapters.TaskListBaseAdapter;
import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.Controllers.TaskController;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;
import il.ac.shenkar.kerenor.tasksapp.Listeners.OnDataSourceChangeListener;
import il.ac.shenkar.kerenor.tasksapp.Listeners.RecyclerItemClickListener;
import il.ac.shenkar.kerenor.tasksapp.R;
import il.ac.shenkar.kerenor.tasksapp.Utils.Utils;
import com.parse.ParseUser;

import java.util.List;

/**
 * TabFragmentAllTasks.java - a class that control the "all tasks" list
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TabFragmentAllTasks extends Fragment implements OnDataSourceChangeListener {
    private RecyclerView mRecyclerView;
    private TaskListBaseAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TaskController controller;
    ParseController parseController;
    private View mView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_all_tasks, container, false);
        initializeView(); // Inflate the layout for this fragment
        return mView;
    }


    private void initializeView(){
        mRecyclerView = (RecyclerView)mView.findViewById(R.id.my_recycler_view);
        controller = new TaskController(getContext());
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskListBaseAdapter(getContext(), controller.getAllTasks(), true);
        mRecyclerView.setAdapter(mAdapter);
        controller.registerOnDataSourceChanged(this);
        parseController = new ParseController(getContext());
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_fresh_all_tasks);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (parseController.checkIsManager()) // manager
                {
                    Log.i("fragment all tasks", "bring manager data from parse");
                    String teamName = ParseUser.getCurrentUser().get("team").toString();
                    Utils.checkUpdatesForManager(parseController, controller, getContext(), teamName);
                }

                else { // employee
                    Log.i("fragment all tasks", "bring employee data from parse");
                    String eMail = ParseUser.getCurrentUser().get("email").toString();
                    Utils.checkUpdatesForEmployee(parseController, controller, getContext(), eMail);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        int listSize = mAdapter.getTasksList().size();
        TextView all_tasks_number = (TextView)mView.findViewById(R.id.all_tasks_field);
        all_tasks_number.setText(String.valueOf(listSize));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiverSort,
                new IntentFilter(Constants.BROADCAST_UPDATE_ACTIVITY_TO_FRAGMENT_SORT));


         /* when we click on an item */
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    List<TaskItem> tempList = mAdapter.getTasksList();

                    TaskCategory category = tempList.get(position).getTaskCategory(); // convert from eNum to string
                    String newCategory = category.toString();
                    TaskPriority priority = tempList.get(position).getTaskPriority();
                    String newPriority = priority.toString();
                    TaskAccept accept = tempList.get(position).getTaskAccept();
                    String newAccept = accept.toString();
                    TaskStatus status = tempList.get(position).getTaskStatus();
                    String newStatus = status.toString();

                    String tempLocation = tempList.get(position).getLocation();
                    if (tempLocation.charAt(0) == '*') tempLocation = tempLocation.substring(1);

                    Intent editTaskIntent = new Intent(Constants.BROADCAST_EDIT_FROM_FRAGMENT_TO_ACTIVITY);

                    editTaskIntent.putExtra(Constants.TASK_NAME_TEXT, tempList.get(position).getTaskName());
                    editTaskIntent.putExtra(Constants.MEMBER_NAME_TEXT, tempList.get(position).getMemberName());
                    editTaskIntent.putExtra(Constants.TASK_LOCATION_TEXT, tempLocation);
                    editTaskIntent.putExtra(Constants.DUE_TIME_TEXT, tempList.get(position).getDueDate());
                    editTaskIntent.putExtra(Constants.CATEGORY_SPINNER, newCategory);
                    editTaskIntent.putExtra(Constants.PRIORITY_RADIO, newPriority);
                    editTaskIntent.putExtra(Constants.STATUS_RADIO, newStatus);
                    editTaskIntent.putExtra(Constants.ACCEPT_RADIO, newAccept);
                    editTaskIntent.putExtra("requestCode", Constants.UPDATE_TASK);
                    editTaskIntent.putExtra("taskId", tempList.get(position).getTaskId());

                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(editTaskIntent);

                }
            })
        );
    } // end of initializeView



    @Override
    public void dataSourceChanged() {
        if (mAdapter != null) {
            mAdapter.updateDataSource(controller.getAllTasks());

            int listSize = controller.getAllTasks().size();
            TextView all_tasks_number = (TextView)mView.findViewById(R.id.all_tasks_field);
            all_tasks_number.setText(String.valueOf(listSize));

            mAdapter.notifyDataSetChanged();
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dataSourceChanged();
        }
    };



    private BroadcastReceiver mMessageReceiverSort = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String sortType = intent.getStringExtra("sort");
            switch(sortType) {
                case "byTime" : {
                    mAdapter.updateDataSource(controller.getAllTasks());
                    break;
                }
                case "byStatus" : {
                    mAdapter.updateDataSource(controller.getAllTasksByStatus());
                    break;
                }
                case "byPriority" : {
                    mAdapter.updateDataSource(controller.getAllTasksByPriority());
                    break;
                }

            }
            mAdapter.notifyDataSetChanged();
        }
    };





    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


}
