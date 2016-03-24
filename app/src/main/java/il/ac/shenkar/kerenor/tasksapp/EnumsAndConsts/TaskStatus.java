package il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts;


public enum TaskStatus {
    WAITING("WAITING", 0),
    IN_PROGRESS("IN_PROGRESS", 1),
    DONE("DONE", 2);

    private String stringValue;
    private int intValue;

    private TaskStatus(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
