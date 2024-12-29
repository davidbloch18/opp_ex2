package gym.management.Employee.Secretary;

import gym.ForumType;
import gym.Gender;
import gym.Gym;
import gym.Person;
import gym.SessionType;
import gym.customers.Client;
import gym.Exception.*;
import gym.management.Employee.Instructor.Instructor;
import gym.management.Sessions.*;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String convertToISOFormat(String dateTimeString) {
        String inputFormat = "dd-MM-yyyy HH:mm";
        // Define the formatter for parsing the input format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);

        // Parse the input string to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);

        // Format to ISO_LOCAL_DATE_TIME
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 16);
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
            NotificationCenter.logAction(
                    "Created new session: " + type + " on " + convertToISOFormat(dateAndTime) + " with instructor: "
                            + session.getInstructor().getPerson().getName());
            return session;
        }
        return null;
    }

    public void registerClientToLesson(Client client, Session session)
            throws DuplicateClientException, ClientNotRegisteredException, NullPointerException {
        List<String> errorMessages = new ArrayList<>();
        boolean isOk = true;

        // Check if the current user is the Secretary
        if (!(this.equals(Gym.getSecretary()))) {
            if (!this.active) {
                throw new NullPointerException("Error: Former secretaries are not permitted to perform actions");
            } else {
                throw new SecurityException("Only the Secretary can perform actions like registering a Client.");
            }
        }

        // Check if the session is still available in the future
        if (!this.gym_info.isSessionStillAvailable(session)) {
            errorMessages.add("Failed registration: Session is not in the future");
            isOk = false;
        }

        // Check if the client meets the session's gender requirements
        if (!this.gym_info.isSessionForumOk(session, client)) {
            if ((session.getSessionForum().equals(ForumType.Male))
                    || (session.getSessionForum().equals(ForumType.Female))) {
                errorMessages
                        .add("Failed registration: Client's gender doesn't match the session's gender requirements");
                isOk = false;
            }
        }

        // Check if the client's age meets the session's age requirements
        if (session.getSessionForum().equals(ForumType.Seniors) && !client.isSenior()) {
            errorMessages.add(String.format(
                    "Failed registration: Client doesn't meet the age requirements for this session (%s)",
                    session.getSessionForum().toString()));
            isOk = false;
        }

        // Check if the client is already registered for the session
        if (session.getAttendees().contains(client)) {
            throw new DuplicateClientException("Error: The client is already registered for this lesson");
        }

        // Check if the client is registered with the gym
        if (!NotificationCenter.isClientRegisterd(client)) {
            throw new ClientNotRegisteredException(
                    "Error: The client is not registered with the gym and cannot enroll in lessons");
        }

        // Check if the session is full
        if (this.gym_info.isSessionFull(session)) {
            errorMessages.add("Failed registration: No available spots for session");
            isOk = false;
        }

        // Check if the client has enough money to register
        if (!this.gym_info.isClientHasMoney(client, session)) {
            errorMessages.add("Failed registration: Client doesn't have enough balance");
            isOk = false;
        }

        // If no issues found, register the client
        if (isOk) {
            if (gym_info.registerClientToLesson(client, session, this)) {
                NotificationCenter.logAction("Registered client: " + client.getPerson().getName() + " to session: "
                        + session.getSessionType() + " on "
                        + convertToISOFormat(session.getDate_and_Time()) + " for price: "
                        + gym_info.getSessionPrice(session));
            }
        } else {
            // Print all error messages if registration failed
            for (String errorMessage : errorMessages) {
                NotificationCenter.logAction(errorMessage);
            }
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
                            convertToISOFormat(((Session) targetType).getDate_and_Time()), message));
        } else if (targetType instanceof Instructor) {
            NotificationCenter.notifyInstructorObservers(message);
            NotificationCenter.logAction(String.format("A message was sent to all the instructors : %s", message));
        } else if (targetType instanceof String) {
            String date = String.valueOf(targetType);
            try {
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new SimpleDateFormat("dd-MM-yyyy").parse(date));
                NotificationCenter.notifySessionObservers(message, date);
                NotificationCenter.logAction(String
                        .format("A message was sent to everyone registered for a session on %s : %s", formattedDate,
                                message));
            } catch (ParseException e) {
                System.out.println("Date format is incorrect");
            }
        }
    }

    public void paySalaries() {
        Map<Instructor, Integer> unpaidHours = notificationCenter.updateHours();
        gym_info.paySalaries(unpaidHours, this);
        NotificationCenter.logAction("Salaries have been paid to all employees");
    }

    public String get_string(String type) {
        List<String> toString = NotificationCenter.get_string(type);
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
