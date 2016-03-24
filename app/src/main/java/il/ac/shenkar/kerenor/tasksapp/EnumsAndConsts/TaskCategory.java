package il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts;


public enum TaskCategory {
    CLEANING("CLEANING", 0),
    ELECTRICITY("ELECTRICITY", 1),
    COMPUTERS("COMPUTERS", 2),
    GENERAL("GENERAL", 3),
    OTHER("OTHER", 4);

    private String stringValue;
    private int intValue;

    private TaskCategory(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
