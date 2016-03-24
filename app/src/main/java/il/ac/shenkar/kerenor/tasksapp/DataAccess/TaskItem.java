package il.ac.shenkar.kerenor.tasksapp.DataAccess;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskAccept;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskCategory;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskPriority;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.TaskStatus;

/**
 * TaskItem.java - a class that hold a single task item
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TaskItem {
    private int taskId;
    private String taskName;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private TaskAccept taskAccept;
    private TaskCategory taskCategory;
    private String memberName;
    private String dueDate;
    private String location;


    public TaskItem(String taskName, TaskStatus taskStatus, TaskPriority taskPriority,
                    TaskAccept taskAccept, TaskCategory taskCategory, String memberName,
                    String dueDate, String location) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskPriority = taskPriority;
        this.taskAccept = taskAccept;
        this.taskCategory = taskCategory;
        this.memberName = memberName;
        this.dueDate = dueDate;
        this.location = location;
    }

    public TaskItem(int taskId, String taskName, TaskStatus taskStatus, TaskPriority taskPriority,
                    TaskAccept taskAccept, TaskCategory taskCategory, String memberName,
                    String dueDate, String location) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskPriority = taskPriority;
        this.taskAccept = taskAccept;
        this.taskCategory = taskCategory;
        this.memberName = memberName;
        this.dueDate = dueDate;
        this.location = location;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskItemText) {
        this.taskName = taskItemText;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public TaskAccept getTaskAccept() {
        return taskAccept;
    }

    public void setTaskAccept(TaskAccept taskAccept) {
        this.taskAccept = taskAccept;
    }

    public TaskCategory getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(TaskCategory taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
