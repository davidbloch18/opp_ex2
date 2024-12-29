package gym.management.Employee.Instructor;

import gym.Gym;
import gym.Person;
import gym.SessionType;
import gym.management.Employee.Secretary.Observer;

import java.util.*;

public class Instructor implements Observer {
    private int hourly_rate;
    private Person person;
    private ArrayList<SessionType> qualifications = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();

    // Private constructor
    private Instructor(Person p, int hourly_rate, ArrayList<SessionType> qualifications) {
        this.person = p;
        this.hourly_rate = hourly_rate;
        this.qualifications = qualifications;
    }

    // Factory method to create an instance
    public static Instructor createInstructor(Person p, int hourly_rate, ArrayList<SessionType> qualifications) {
        return new Instructor(p, hourly_rate, qualifications);
    }

    public void pay(int amount) {
        this.person.changeBalance(amount);
    }

    // Getters and Setters
    public int getHourlyRate() {
        return hourly_rate;
    }

    public void setHourlyRate(int hourly_rate) {
        this.hourly_rate = hourly_rate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArrayList<SessionType> getQualifications() {
        return this.qualifications;
    }

    public void addQualification(SessionType sessionType) {
        this.qualifications.add(sessionType);
    }

    @Override
    public void update(String message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        String qualifications = this.qualifications.toString();
        return String.format("%s | Role: Instructor | Salary per Hour: %d | Certified Classes:%s ",
                this.person.toString(), this.hourly_rate, qualifications.replaceAll("[\\[\\]]", ""));
    }
}
