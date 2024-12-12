import java.util.ArrayList;
import java.util.List;

public class Instructor {
    private int hourly_rate;
    private List<SessionType> admitted_sessions = new ArrayList<>();
    private Person person;

    // Private constructor
    private Instructor(Person p, int hourly_rate, List<SessionType> admitted_sessions) {
        this.person = p;
        this.hourly_rate = hourly_rate;
        this.admitted_sessions = new ArrayList<>();
    }

    // Factory method to create an instance
    public static Instructor createInstructor(Person p, int hourly_rate, List<SessionType> admitted_sessions) {
        if (!isCallerSecretary()) {
            throw new SecurityException("Only the Secretary can create an Instructor.");
        }
        return new Instructor(p, hourly_rate, admitted_sessions);
    }

    // Helper method to verify the caller
    private static boolean isCallerSecretary() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            String callerClassName = stackTrace[3].getClassName();
            return "com.company.hr.Secretary".equals(callerClassName);
        }
        return false;
    }

    // Getters and Setters
    public int getHourlyRate() {
        return hourly_rate;
    }

    public void setHourlyRate(int hourly_rate) {
        this.hourly_rate = hourly_rate;
    }

    public List<SessionType> getAdmittedSessions() {
        return admitted_sessions;
    }

    public void addAdmittedSession(SessionType sessionType) {
        this.admitted_sessions.add(sessionType);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
