package il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts;


public enum TaskAccept {
    WAITING("WAITING", 0),
    ACCEPT("ACCEPT", 1),
    REJECT("REJECT", 2);

    private String stringValue;
    private int intValue;

    private TaskAccept(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}