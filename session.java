public class Session {
    private String type;
    private String date;
    private String time;
    private Instructor admittedInstructor;

    // Private constructor
    private Session(String type, String date, String time, Instructor admittedInstructor) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.admittedInstructor = admittedInstructor;
    }

    // Factory method to create a new Session
    public static Session createSession(String type, String date, String time, Instructor admittedInstructor) {
        if (!isCallerSecretary()) {
            throw new SecurityException("Only the Secretary can create a Session.");
        }
        return new Session(type, date, time, admittedInstructor);
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Instructor getInstructor() {
        return admittedInstructor;
    }

    public void setInstructor(Instructor admittedInstructor) {
        this.admittedInstructor = admittedInstructor;
    }
}
