package gym.management.Employee.Secretary;

import gym.Exception.ClientNotRegisteredException;
import gym.Exception.DuplicateClientException;
import gym.customers.Client;
import gym.management.Employee.Instructor.Instructor;
import gym.management.Sessions.Session;

import java.util.*;

public class NotificationCenter {
    private static NotificationCenter instance;
    private static Set<Observer> client_observers = new HashSet<>();
    private static Set<Observer> session_observers = new HashSet<>();
    private static Set<Observer> instructor_observer = new HashSet<>();
    private static List<String> actions = new ArrayList<>();

    // Private constructor to prevent instantiation
    private NotificationCenter() {}

    // Static method to get the single instance of NotificationCenter
    protected static NotificationCenter getInstance() {
        if (instance == null) {
            instance = new NotificationCenter();
        }
        return instance;
    }

    // Method to register an observer
    public static void registerObserver(Observer observer)  {
        client_observers.add(observer);
    }

    // Method to unregister an observer
    public static void unregisterObserver(Observer observer) throws ClientNotRegisteredException {
        if(client_observers.contains(observer)){
            client_observers.remove(observer);
        }
        else {
            throw new ClientNotRegisteredException("Error: Registration is required before attempting to unregister");
        }
    }
    public static void registerSessionObserver(Observer observer) {
        session_observers.add(observer);
    }
    public static void registerInstructorObserver(Observer observer) {instructor_observer.add(observer);
    }

    // Method to notify all observers
    protected static void notifyClientObservers(String message) {
        for (Observer observer : client_observers) {
            observer.update(message);
        }
    }
    protected static void notifySessionObservers(String message, String date) {
        for (Observer observer : session_observers) {
            Session s = (Session)observer;
            if(s.get_Date().equals(date)){
                s.update(message);
            }
        }
    }
    protected static void notifyInstructorObservers(String message) {
        for (Observer observer : instructor_observer) {
                observer.update(message);
        }
    }
    protected static void notifyBySession(String message, Session session){
        session.update(message);
    }

    protected static void logAction(String action) {
        actions.add(action);
    }
    protected static List<String> getActionHistory() {
        return actions;
    }

    protected static Set<String> get_string(String type){
        Set<String> toString = new TreeSet<>();
        Set<Observer> pointer = new HashSet<>();
        switch (type){
            case "Clients":
                pointer = client_observers;
                break;
            case "Instructor":
                pointer = instructor_observer;
                break;
            case "Sessions":
                pointer = session_observers;
        }
        for (Observer obs: pointer) {;
            toString.add(obs.toString());
        }
        return toString;
    }

    protected static boolean isClientRegisterd(Client client){
        return client_observers.contains(client);
    }

    protected Map<Instructor,Integer> updateHours(){
        Map<Instructor,Integer> unpaid_hours = new HashMap<>();
        for (Observer session:session_observers) {
            Session s = (Session)session;
            if(!((Session) session).isPaid()){
                Instructor instructor = ((Session) session).getInstructor();
                unpaid_hours.put(instructor, unpaid_hours.getOrDefault(instructor, 0) + 1);
            }

    }
        return unpaid_hours;
    }
}

