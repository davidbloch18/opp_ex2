package gym.management.Employee.Secretary;

import gym.Gym;
import gym.Person;
import gym.customers.Client;
import gym.management.Employee.Instructor.*;
import gym.SessionType;
import gym.management.Sessions.Session;
import gym.Exception.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Gym_Info {
    private int balance = 9420;
    private List<Session> gym_sessions;
    private Map<String, Integer> sessions_max_participants;
    private Map<String, Integer> sessions_prices;

    Gym_Info() {
        this.balance = 0;
        this.gym_sessions = new ArrayList<>();

        this.sessions_max_participants = new HashMap<>();
        this.sessions_max_participants.put((String.valueOf(SessionType.Pilates)), 30);
        this.sessions_max_participants.put((String.valueOf(SessionType.MachinePilates)), 10);
        this.sessions_max_participants.put((String.valueOf(SessionType.ThaiBoxing)), 20);
        this.sessions_max_participants.put((String.valueOf(SessionType.Ninja)), 5);

        this.sessions_prices = new HashMap<>();
        sessions_prices.put(String.valueOf(SessionType.Pilates), 60);
        sessions_prices.put((String.valueOf(SessionType.MachinePilates)), 80);
        sessions_prices.put((String.valueOf(SessionType.ThaiBoxing)), 100);
        sessions_prices.put((String.valueOf(SessionType.Ninja)), 150);
    }

    private void ensureSecretaryAccess(Secretary caller) {
        if (!Gym.getSecretary().equals(caller)) {
            throw new SecurityException("Access denied: Only the Secretary can perform this action.");
        }
    }

    protected void setBalance(int balance, Secretary caller) {
        ensureSecretaryAccess(caller);
        this.balance = balance;
    }

    protected boolean add_session(Session session, Secretary caller) {
        ensureSecretaryAccess(caller);
        this.gym_sessions.add(session);
        return true;
    }

    protected boolean registerClient(Client client, Secretary caller)
            throws SecurityException, DuplicateClientException {
        ensureSecretaryAccess(caller);
        client.getPerson().register();
        NotificationCenter.registerObserver(client);
        return true;
    }

    protected boolean unregisterClient(Client client, Secretary caller)
            throws SecurityException, ClientNotRegisteredException {
        ensureSecretaryAccess(caller);
        client.getPerson().unregister();
        NotificationCenter.unregisterObserver(client);
        return true;
    }

    protected boolean hireInstructor(Instructor instructor, Secretary caller) {
        ensureSecretaryAccess(caller);
        NotificationCenter.registerInstructorObserver(instructor);
        return true;
    }

    protected boolean registerClientToLesson(Client client, Session session, Secretary caller) {
        ensureSecretaryAccess(caller);
        session.addAttendee(client);
        client.getPerson().changeBalance(-this.getSessionPrice(session));
        this.changeGymBalance(this.getSessionPrice(session));
        return true;
    }

    protected void unregisterClientFromLesson(Client client, Session session, Secretary caller)
            throws ClientNotRegisteredException {
        ensureSecretaryAccess(caller);
        if (session.getAttendees().contains(client)) {
            session.getAttendees().remove(client);
            client.getPerson().changeBalance(this.getSessionPrice(session));
            NotificationCenter
                    .logAction("change balance for " + client.getName() + " " + this.getSessionPrice(session));
            this.balance -= this.getSessionPrice(session);
        } else {
            throw new ClientNotRegisteredException("Error: client not registered to this lesson");
        }
    }

    protected boolean isSessionFull(Session session) {
        return this.getMaxParticipants(session.getSessionType()) <= session.getAttendees().size();
    }

    protected boolean isClientHasMoney(Client client, Session session) {
        return client.getPerson().getBalance() >= this.sessions_prices.get(session.getSessionType().toString());
    }

    protected static boolean isSessionStillAvailable(Session session) {
        String[] possibleFormats = { "yyyy-MM-dd HH:mm", "dd-MM-yyyy HH:mm" };
        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDateTime sessionDateTime = LocalDateTime.parse(session.getDate_and_Time(), formatter);
                LocalDateTime now = LocalDateTime.now();
                return sessionDateTime.isAfter(now);
            } catch (Exception ignored) {
                // Try the next format
            }
        }
        System.out.println("Invalid date format for session: " + session.getDate_and_Time());
        return false;
    }

    protected boolean isSessionForumOk(Session session, Client client) {
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
                return person.getAge() >= 65;
            case "All":
                return true;
            default:
                return true;
        }
    }

    protected void paySalaries(Map<Instructor, Integer> unpaid_hours, Secretary caller) {
        ensureSecretaryAccess(caller);
        for (Map.Entry<Instructor, Integer> unpaid : unpaid_hours.entrySet()) {
            Instructor instructor = unpaid.getKey();
            int rate = instructor.getHourlyRate();
            int salary = rate * unpaid.getValue();
            instructor.getPerson().changeBalance(salary);
            this.balance -= salary;
        }
        caller.pay();
        this.balance -= caller.getSalary();
    }

    protected int getBalance(Secretary caller) {
        ensureSecretaryAccess(caller);

        return this.balance;
    }

    protected int getSessionPrice(Session session) {
        return this.sessions_prices.get(session.getSessionType().toString());
    }

    private void changeGymBalance(int amount) {
        this.balance += amount;
    }

    protected int getMaxParticipants(SessionType sessionType) {
        return sessions_max_participants.get(sessionType.toString());
    }
}
