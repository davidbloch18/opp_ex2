package gym.management.Employee.Secretary;

import gym.ForumType;
import gym.Gym;
import gym.Person;
import gym.SessionType;
import gym.customers.Client;
import gym.Exception.*;
import gym.management.Employee.Instructor.Instructor;
import gym.management.Sessions.*;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Secretary {
    private boolean active;
    private Person person;
    private int salary;
    private static Secretary instance;
    private NotificationCenter notificationCenter;
    private Gym_Info gym_info;

    private Secretary(Person person, int salary) {
        this.person = person;
        this.salary = salary;
        this.active = true;
        this.gym_info = new Gym_Info();
        this.notificationCenter = NotificationCenter.getInstance();
    }

    public static Secretary createInstance(Person person, int salary) {

        if (!(instance == null)) {
            Gym_Info temp = instance.getGymInfo();
            Secretary newSec = new Secretary(person, salary);
            newSec.setGymInfo(temp);
            instance = null;
            instance = newSec;
        } else {
            Secretary newSec = new Secretary(person, salary);
            instance = newSec;
        }

        NotificationCenter.logAction("A new secretary has started working at the gym: " + person.getName());
        return instance;
    }

    public void changeActive() {
        this.active = !this.active;
    }

    public Client registerClient(Person person) throws InvalidAgeException, DuplicateClientException {
        Client new_client = Client.createClient(person, this);
        if (this.gym_info.registerClient(new_client, this)) {
            NotificationCenter.logAction("Registered new client: " + person.getName());
            return new_client;
        }
        return null;
    }

    public void unregisterClient(Client client) throws ClientNotRegisteredException, SecurityException {
        if (this.gym_info.unregisterClient(client, this)) {
            client.changeActive();
            NotificationCenter.logAction("Unregistered client: " + client.getPerson().getName());
        }
    }

    public Instructor hireInstructor(Person person, int hourlyRate, ArrayList<SessionType> qulifications)
            throws SecurityException {
        Instructor newInstructor = Instructor.createInstructor(person, hourlyRate, qulifications);
        if (this.gym_info.hireInstructor(newInstructor, this)) {
            NotificationCenter.registerInstructorObserver(newInstructor);
            NotificationCenter
                    .logAction("Hired new instructor: " + person.getName() + " with salary per hour: " + hourlyRate);
            return newInstructor;
        }
        return null;
    }

    public Session addSession(SessionType type, String dateAndTime, ForumType forum, Instructor instructor)
            throws InstructorNotQualifiedException, SecurityException {

        Session session = Session.createSession(type, dateAndTime, instructor, forum);
        if (this.gym_info.add_session(session, this)) {
            NotificationCenter.registerSessionObserver(session);
            NotificationCenter.logAction("Created new session: " + type + " on " + dateAndTime + " with instructor: "
                    + session.getInstructor().getPerson().getName());
            return session;
        }
        return null;
    }

    public void registerClientToLesson(Client client, Session session)
            throws NullPointerException {
        Map<String, String> exceptions = new HashMap<>();

        // Check if the current object is the correct secretary
        if (!(this.equals(Gym.getSecretary()))) {
            if (!this.active) {
                throw new NullPointerException("Error: Former secretaries are not permitted to perform actions");
            } else {
                exceptions.put("SecurityException", "Only the Secretary can register a Client.");
            }
        }

        // Check if the client is already registered for the session
        if (session.getAttendees().contains(client)) {
            exceptions.put("DuplicateClientException", "The client is already registered for this lesson.");
        }

        // Check if the client is registered with the gym
        if (!NotificationCenter.isClientRegisterd(client)) {
            exceptions.put("ClientNotRegisteredException",
                    "The client is not registered with the gym and cannot enroll in lessons.");
        }

        // Check if the session is full
        if (this.gym_info.isSessionFull(session)) {
            exceptions.put("SessionFullException", "No available spots for session.");
            NotificationCenter.logAction("Failed registration: No available spots for session");
        }

        // Check if the session is in the future
        if (!this.gym_info.isSessionStillAvailable(session)) {
            exceptions.put("SessionNotAvailableException", "Session is not in the future.");
            NotificationCenter.logAction("Failed registration: Session is not in the future");
        }

        // Check if the session's forum matches the client's requirements
        if (!this.gym_info.isSessionForumOk(session, client)) {
            if ((session.getSessionForum().equals(ForumType.Male))
                    || (session.getSessionForum().equals(ForumType.Female))) {
                exceptions.put("ForumMismatchException",
                        "Client's gender doesn't match the session's gender requirements.");
                NotificationCenter.logAction(
                        "Failed registration: Client's gender doesn't match the session's gender requirements");
            } else if (session.getSessionForum().equals(ForumType.Seniors)) {
                exceptions.put("AgeMismatchException", String.format(
                        "Client doesn't meet the age requirements for this session (%s).",
                        session.getSessionForum().toString()));
                NotificationCenter.logAction(String.format(
                        "Failed registration: Client doesn't meet the age requirements for this session (%s)",
                        session.getSessionForum().toString()));
            }
        }

        // Check if the client has enough balance
        if (!this.gym_info.isClientHasMoney(client, session, this)) {
            exceptions.put("InsufficientBalanceException", "Client doesn't have enough balance.");
            NotificationCenter.logAction("Failed registration: Client doesn't have enough balance");
        }

        // If there are no exceptions, proceed with registration
        if (exceptions.isEmpty()) {
            if (gym_info.registerClientToLesson(client, session, this)) {
                NotificationCenter.logAction("Registered client: " + client.getPerson().getName() + " to session: "
                        + session.getSessionType() + " on " + session.getDate_and_Time() + " for price "
                        + gym_info.getSessionPrice(session));
            }
        } else {
            // Print all the exceptions
            System.out.println("Registration failed for the following reasons:");
            exceptions.forEach((key, value) -> System.out.println(key + ": " + value));
        }
    }

    public void unregisterClientFromLesson(Client client, Session session) throws ClientNotRegisteredException {
        this.gym_info.unregisterClientFromLesson(client, session, this);
        NotificationCenter.logAction("Unregistered client: " + client.getPerson().getName() + " from session: "
                + session.getSessionType());
    }

    public void notActive() {
        instance = null;
    }

    private Gym_Info getGymInfo() {
        return gym_info;
    }

    private void removeGymInfo() {
        this.gym_info = null;
    }

    private void setGymInfo(Gym_Info gym_info) {
        this.gym_info = gym_info;
    }

    public void notify(String message) {
        NotificationCenter.notifyClientObservers(message);
        NotificationCenter.logAction("A message was sent to all gym clients: " + message);
    }

    public void notify(Object targetType, String message) {
        if (targetType instanceof Session) {
            NotificationCenter.notifyBySession(message, (Session) targetType);
            NotificationCenter
                    .logAction(String.format("A message was sent to everyone registered for session %s on %s : %s",
                            ((Session) targetType).getSessionType().toString(),
                            ((Session) targetType).getDate_and_Time(), message));
        } else if (targetType instanceof Instructor) {
            NotificationCenter.notifyInstructorObservers(message);
            NotificationCenter.logAction(String.format("A message was sent to all the instructors : %s", message));
        } else if (targetType instanceof String) {
            String date = String.valueOf(targetType);
            NotificationCenter.notifySessionObservers(message, date);
            NotificationCenter.logAction(String
                    .format("A message was sent to everyone registered for a session on %s : %s", targetType, message));
        }
    }

    public void paySalaries() {
        Map<Instructor, Integer> unpaidHours = notificationCenter.updateHours();
        System.out.println(unpaidHours.toString() + " ddd");
        gym_info.paySalaries(unpaidHours, this);
        NotificationCenter.logAction("Salaries have been paid to all employees");
    }

    public String get_string(String type) {
        Set<String> toString = NotificationCenter.get_string(type);
        StringBuilder string = new StringBuilder();
        for (String obj : toString) {
            string.append("\n");
            string.append(obj);
        }
        return string.toString();
    }

    protected int getSalary() {
        return salary;
    }

    protected void pay() {
        person.changeBalance(salary);
    }

    public void printActions() {
        List<String> actionsList = NotificationCenter.getActionHistory();
        for (String action : actionsList) {
            System.out.println(action);
        }
    }

    public void setActive() {
        this.active = !this.active;
    }

    public int getGymMoney() {
        return gym_info.getBalance(this);
    }

    @Override
    public String toString() {
        return String.format("%s | Role: Secretary | Salary per Month: %d", this.person.toString(), salary);
    }

    public int getMaxParticipants(SessionType sessionType) {
        return gym_info.getMaxParticipants(sessionType);
    }
}
