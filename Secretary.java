import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Secretary {
    private Person person;
    private int salary;
    private static Secretary instance;
    private List<Instructor> hired_instructors;
    private Map<String, Integer> sessionPricing;
    private int gymMoney = -1806;

    {
        sessionPricing = new HashMap<>();
        sessionPricing.put("pilates", 60);
        sessionPricing.put("MachinePilates", 80);
        sessionPricing.put("ThaiBoxing", 100);
        sessionPricing.put("Ninja", 150);
    }
    private Map<String, Integer> sessionAttendees;

    {
        sessionAttendees = new HashMap<>();
        sessionAttendees.put("pilates", 30);
        sessionAttendees.put("MachinePilates", 10);
        sessionAttendees.put("ThaiBoxing", 20);
        sessionAttendees.put("Ninja", 5);
    }

    private Secretary(Person person, int salary) {
        this.person = person;
        this.salary = salary;
    }

    public static Secretary createInstance(Person person, int salary) {
        if (instance != null) {
            System.out.println("Previous Secretary instance is now invalidated.");
        }
        instance = new Secretary(person, salary);
        return instance;
    }

    // Static method to get the current active instance
    public static Secretary getInstance() {
        return instance;
    }

    public Client registerClient(Person person) {
        return Client.createClient(person);
    }

    public void unregisterClient(Client client) {
        Client.removeClient(client);
    }

    public List<Client> getActiveClients() {
        return Client.getActiveClients();
    }

    // Method to hire an instructor
    public Instructor hireInstructor(Person person, int hourlyRate, List<SessionType> admittedSessions) {
        Instructor newInstructor = Instructor.createInstructor(person, hourlyRate, admittedSessions);
        hired_instructors.add(newInstructor);
        return newInstructor;
    }

    // Method to find an instructor by their admitted sessions
    public Optional<Instructor> findInstructorByAdmittedSessions(List<Session> admittedSessions) {
        return hired_instructors.stream()
                .filter(instructor -> instructor.getAdmittedSessions().containsAll(admittedSessions))
                .findFirst();
    }

    // Method to create a session
    public Session addSession(SessionType Type, String date_and_time,
            ForumType forum, Instructor instructor) {
        Session session = Session.createSession(Type, date_and_time, instructor, forum);
        instructor.getAdmittedSessions().add(Type);
        return session;
    }

    public void registerClientToLesson(Client client, Session session) {
        if (!isSessionFull(session) && isClientHasMoney(client, session) && isSessionStillAvailable(session)
                && isSessionForumOk(session, client)) {
            session.getAttendees().add(client);
            client.getPerson().setMoneyLeft(
                    client.getPerson().getMoneyLeft() - sessionPricing.get(session.getSessionType().toString()));
            gymMoney += sessionPricing.get(session.getSessionType().toString());
        }
    }

    private void unregisterClientFromLesson(Client client, Session session) {
        if (session.getAttendees().contains(client)) {
            session.getAttendees().remove(client);
            client.getPerson().setMoneyLeft(
                    client.getPerson().getMoneyLeft() + sessionPricing.get(session.getSessionType().toString()));
            gymMoney -= sessionPricing.get(session.getSessionType().toString());
        }
    }

    private boolean isSessionFull(Session session) {
        return sessionAttendees.get(session.getSessionType().toString()) <= session.getAttendees().size();

    }

    private boolean isClientHasMoney(Client client, Session session) {
        return client.getPerson().getMoneyLeft() >= sessionPricing.get(session.getSessionType().toString());
    }

    private boolean isSessionStillAvailable(Session session) {
        // Define the formatter used in session date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Combine the session's date and time into a single LocalDateTime
        LocalDateTime sessionDateTime = LocalDateTime.parse(session.getDate_and_Time(), formatter);

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Compare the session's date and time with the current date and time
        return sessionDateTime.isAfter(now);
    }

    private boolean isSessionForumOk(Session session, Client client) {
        String forum = session.getSessionForum().toString();
        Person person = client.getPerson();

        if (forum == null || person == null || person.getGender() == null) {
            return false; // Handle null cases
        }

        switch (forum) {
            case "Female":
                return "Female".equals(person.getGender()); // note - this is bad solution not good equals
            case "Male":
                return "Male".equals(person.getGender());
            case "Seniors":
                return person.isElderly();
            case "All":
                return true;
            default:
                return false;
        }
    }

    public void paySalaries() {
        for (Instructor instructor : hired_instructors) {
            instructor.getPerson().setMoneyLeft(instructor.getPerson().getMoneyLeft() + instructor.getHourlyRate());
            gymMoney -= instructor.getHourlyRate();
        }
        person.setMoneyLeft(salary = +person.getMoneyLeft());
        gymMoney -= salary;
        System.out.println(gymMoney + "is the gym's money");

    }

}
