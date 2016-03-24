package il.ac.shenkar.kerenor.tasksapp.DataAccess;

/**
 * TeamMember.java - a class that hold a single team member (employee)
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class TeamMember {
    private String eMail;
    private String phoneNumber;
    private boolean isRegistered;


    public TeamMember(String eMail, String phoneNumber, boolean isRegistered) {
        super();
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;
        this.isRegistered = isRegistered;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
}
