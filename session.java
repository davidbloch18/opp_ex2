import java.util.List;

public class Session {

    private SessionType sessionType;
    private ForumType sessionForum;
    private String date_and_time;
    private Instructor admittedInstructor;
    private List<Client> attendees;

    // Private constructor
    private Session(SessionType sessionType, String date_and_time, Instructor admittedInstructor,
            ForumType sessionForum) {
        this.sessionType = sessionType;
        this.sessionForum = sessionForum;
        this.date_and_time = date_and_time;
        this.admittedInstructor = admittedInstructor;
    }

    // Factory method to create a new Session
    public static Session createSession(SessionType SessionType, String date_and_time,
            Instructor admittedInstructor, ForumType sessionForum) {
        if (!isCallerSecretary()) {
            throw new SecurityException("Only the Secretary can create a Session.");
        }
        return new Session(SessionType, date_and_time, admittedInstructor, sessionForum);
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
    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getDate_and_Time() {
        return date_and_time;
    }

    public void setDate_and_Time(String date_and_time) {
        this.date_and_time = date_and_time;
    }

    public void setInstructor(Instructor admittedInstructor) {
        this.admittedInstructor = admittedInstructor;
        // check the session with the instructor's admitted sessions
    }

    public Instructor getInstructor() {
        return admittedInstructor;
    }

    public void addAttendee(Client client) {
        attendees.add(client);
    }

    public void removeAttendee(Client client) {
        attendees.remove(client);
    }

    public int getAttendeesCount() {
        return attendees.size();
    }

    public List<Client> getAttendees() {
        return attendees;
    }

    public ForumType getSessionForum() {
        return sessionForum;
    }

    public void setSessionForum(ForumType sessionForum) {
        this.sessionForum = sessionForum;
    }

}
