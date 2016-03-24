package il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts;


public enum TaskPriority {
    NORMAL("NORMAL", 0),
    LOW("LOW", 1),
    URGENT("URGENT", 2);

    private String stringValue;
    private int intValue;

    private TaskPriority(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
