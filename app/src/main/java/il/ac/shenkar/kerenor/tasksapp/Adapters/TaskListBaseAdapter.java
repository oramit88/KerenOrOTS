package il.ac.shenkar.kerenor.tasksapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import il.ac.shenkar.kerenor.tasksapp.DataAccess.TaskItem;
import il.ac.shenkar.kerenor.tasksapp.R;
import com.parse.ParseUser;

/**
 * TaskListBaseAdapter.java - a class that control the RecyclerView of the tasks lists
 * (waiting tasks / all tasks)
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TaskListBaseAdapter extends RecyclerView.Adapter<TaskListBaseAdapter.ViewHolder> {
    private List<TaskItem> tasksList;
    private Context context;
    private boolean isAllTab;


    public TaskListBaseAdapter(Context context, List<TaskItem> tasksList, boolean isAllTab) {
        this.tasksList = tasksList;
        this.context = context;
        this.isAllTab = isAllTab;
    }


    public List<TaskItem> getTasksList() {
        return tasksList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView task_name_textView;
        TextView member_name_textView;
        TextView due_time_textView;
        TextView status_textView;
        ImageView task_done_or_new_ImageView;



        public ViewHolder(View parentView) {
            super(parentView);
            task_name_textView = (TextView) parentView.findViewById(R.id.task_name_task_item_field);
            due_time_textView = (TextView) parentView.findViewById(R.id.due_time_task_item_field);
            status_textView = (TextView) parentView.findViewById(R.id.current_status_tasks_item_field);
            member_name_textView = (TextView) parentView.findViewById(R.id.member_task_item_field);
            task_done_or_new_ImageView = (ImageView) parentView.findViewById(R.id.task_done_or_new_image);

            task_done_or_new_ImageView.setVisibility(View.GONE);

            boolean isManager = (boolean) ParseUser.getCurrentUser().get("isManager");

            if (!isAllTab) {
                parentView.findViewById(R.id.current_status_tasks_item_text).setVisibility(View.GONE);
                status_textView.setVisibility(View.GONE);
            }

            if (!isManager) {
                parentView.findViewById(R.id.member_task_item_text).setVisibility(View.GONE);
                member_name_textView.setVisibility(View.GONE);
            }

//            done_button = (Button) parentView.findViewById(R.id.doneButton);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskItem taskItem = tasksList.get(position);

        holder.task_done_or_new_ImageView.setVisibility(View.GONE);

        holder.task_name_textView.setText(taskItem.getTaskName());
        holder.due_time_textView.setText(taskItem.getDueDate());
        holder.status_textView.setText(taskItem.getTaskStatus().toString());
        holder.member_name_textView.setText(taskItem.getMemberName());


        if (taskItem.getLocation().charAt(0) == '*') {
            holder.task_done_or_new_ImageView.setVisibility(View.VISIBLE);
            holder.task_done_or_new_ImageView.setImageResource(R.mipmap.ic_new_task_1);
        }

    }


    @Override
    public int getItemCount() {
        return tasksList.size();
    }


    public void updateDataSource(List<TaskItem> tasksList)
    {
        if (tasksList == null) return;
        this.tasksList = tasksList;
    }




}

















