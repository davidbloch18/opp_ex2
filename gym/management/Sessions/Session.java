package gym.management.Sessions;

import gym.Exception.*;
import gym.ForumType;
import gym.Gym;
import gym.SessionType;
import gym.customers.Client;
import gym.management.Employee.Instructor.*;
import gym.management.Employee.Secretary.Observer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Session implements Observer {

    private SessionType sessionType;
    private ForumType sessionForum;
    private String date_and_time;
    private Instructor instructor;
    private Set<Client> attendees;
    private boolean add_pay;

    // Private constructor
    Session(SessionType sessionType, String date_and_time, Instructor instructor,
            ForumType sessionForum) {
        this.sessionType = sessionType;
        this.sessionForum = sessionForum;
        this.date_and_time = date_and_time;
        this.instructor = instructor;
        this.attendees = new HashSet<>();
        this.add_pay = false;
    }

    // Factory method to create a new Session
    public static Session createSession(SessionType sessionType, String date_and_time,
                                        Instructor instructor, ForumType sessionForum) throws InstructorNotQualifiedException{
        if (!instructor.getQualifications().contains(sessionType)){
            throw new InstructorNotQualifiedException("Error: Instructor is not qualified to conduct this session type.");
        }
        else{
            return new Session(sessionType, date_and_time, instructor, sessionForum);
        }
    }

    public void added(){
        this.add_pay = true;
    }
    public boolean isPaid(){
        return this.add_pay;
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
    public String get_Date(){
        return date_and_time.split(" ")[0];
    }

    public void setDate_and_Time(String date_and_time) {
        this.date_and_time = date_and_time;
    }
/*
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        // check the session with the instructor's admitted sessions
    }

 */

    public Instructor getInstructor() {
        return this.instructor;
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

    public Set<Client> getAttendees() {
        return attendees;
    }

    public ForumType getSessionForum() {
        return sessionForum;
    }

    public void setSessionForum(ForumType sessionForum) {
        this.sessionForum = sessionForum;
    }

    @Override
    public void update(String message) {
        for (Client client: this.attendees) {
            client.update(message);
        }
    }

    @Override
    public String toString(){
        String participants = String.format("%d/%d",this.attendees.size(), Gym.getSecretary().getMaxParticipants(this.sessionType));
        return String.format("Session Type: %s | Date: %s | Forum: %s | Instructor: %s | Participants: %s",
                this.sessionType.toString(), this.date_and_time,this.sessionForum, instructor.getPerson().getName(), participants);
    }
}

