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
    private List<Session> sessions;
    private Map<String, Integer> sessionPricing;
    private int gymMoney = -1806;
    private List<String> actions;

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
        instance.actions.add("A new secretary has started working at the gym: " + person.getName());
        return instance;
    }

    // Static method to get the current active instance
    public static Secretary getInstance() {
        return instance;
    }

    public Client registerClient(Person person) {
        Client newClient = Client.createClient(person);
        actions.add("Registered new client: " + person.getName());
        return newClient;
    }

    public void unregisterClient(Client client) {
        Client.removeClient(client);
        actions.add("Unregistered client: " + client.getPerson().getName());
    }

    public List<Client> getActiveClients() {
        return Client.getActiveClients();
    }

    // Method to hire an instructor
    public Instructor hireInstructor(Person person, int hourlyRate, List<SessionType> admittedSessions) {
        Instructor newInstructor = Instructor.createInstructor(person, hourlyRate, admittedSessions);
        hired_instructors.add(newInstructor);
        actions.add("Hired new instructor: " + person.getName() + " with salary per hour: " + hourlyRate);
        return newInstructor;
    }

    // Method to find an instructor by their admitted sessions
    public Optional<Instructor> findInstructorByAdmittedSessions(List<Session> admittedSessions) {
        return hired_instructors.stream()
                .filter(instructor -> instructor.getAdmittedSessions().containsAll(admittedSessions))
                .findFirst();
    }

    // Method to create a session
    public Session addSession(SessionType type, String dateAndTime, ForumType forum, Instructor instructor) {
        Session session = Session.createSession(type, dateAndTime, instructor, forum);
        instructor.getAdmittedSessions().add(type);
        sessions.add(session);
        actions.add("Created new session: " + type + " on " + dateAndTime + " with instructor: "
                + instructor.getPerson().getName());
        return session;
    }

    public void registerClientToLesson(Client client, Session session) {
        if (!isSessionFull(session) && isClientHasMoney(client, session) && isSessionStillAvailable(session)
                && isSessionForumOk(session, client) && !session.getAttendees().contains(client) && client.isActive()) {
            session.getAttendees().add(client);
            client.getPerson().setMoneyLeft(
                    client.getPerson().getMoneyLeft() - sessionPricing.get(session.getSessionType().toString()));
            gymMoney += sessionPricing.get(session.getSessionType().toString());
            actions.add("Registered client: " + client.getPerson().getName() + " to session: "
                    + session.getSessionType() + " on " + session.getDate_and_Time());
        }
    }

    private void unregisterClientFromLesson(Client client, Session session) {
        if (session.getAttendees().contains(client)) {
            session.getAttendees().remove(client);
            client.getPerson().setMoneyLeft(
                    client.getPerson().getMoneyLeft() + sessionPricing.get(session.getSessionType().toString()));
            gymMoney -= sessionPricing.get(session.getSessionType().toString());
            actions.add("Unregistered client: " + client.getPerson().getName() + " from session: "
                    + session.getSessionType());
        }
    }

    private boolean isSessionFull(Session session) {
        return sessionAttendees.get(session.getSessionType().toString()) <= session.getAttendees().size();
    }

    private boolean isClientHasMoney(Client client, Session session) {
        return client.getPerson().getMoneyLeft() >= sessionPricing.get(session.getSessionType().toString());
    }

    private boolean isSessionStillAvailable(Session session) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime sessionDateTime = LocalDateTime.parse(session.getDate_and_Time(), formatter);
        LocalDateTime now = LocalDateTime.now();
        return sessionDateTime.isAfter(now);
    }

    private boolean isSessionForumOk(Session session, Client client) {
        String forum = session.getSessionForum().toString();
        Person person = client.getPerson();

        if (forum == null || person == null || person.getGender() == null) {
            return false;
        }

        switch (forum) {
            case "Female":
                return "Female".equals(person.getGender());
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
            actions.add("Paid salary to instructor: " + instructor.getPerson().getName() + " Salary: "
                    + instructor.getHourlyRate());
        }
        person.setMoneyLeft(salary = +person.getMoneyLeft());
        gymMoney -= salary;
        actions.add("Paid salary to secretary: " + person.getName() + " Salary: " + salary);
    }

    public void notify(String message) {
        for (Client client : getActiveClients()) {
            client.receiveMessage(message);
        }
        actions.add("A message was sent to all gym clients: " + message);
    }

    private void notifyClientsBySession(Session session, String message) {
        for (Client client : session.getAttendees()) {
            client.receiveMessage(message);
        }
        actions.add("A message was sent to everyone registered for session " + session.getSessionType() + " on "
                + session.getDate_and_Time() + ": " + message);
    }

    private void notifyClientsByDate(LocalDateTime dateTime, String message) {
        for (Session session : sessions) {
            if (session.getDate_and_Time().equals(dateTime)) {
                notifyClientsBySession(session, message);
            }
        }
    }

    public void notify(Object targetType, String message) {
        if (targetType instanceof Session) {
            notifyClientsBySession((Session) targetType, message);
        } else if (targetType instanceof String) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse((String) targetType,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                notifyClientsByDate(dateTime, message);
            } catch (Exception e) {
                notify(message);
            }
        }
    }

    public void printActions() {
        for (String action : actions) {
            System.out.println(action);
        }
    }
}
