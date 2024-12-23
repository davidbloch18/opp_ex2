package gym.customers;

import gym.Exception.*;
import gym.Gym;
import gym.Person;
import gym.management.Employee.Secretary.Observer;
import gym.management.Employee.Secretary.Secretary;


import java.util.ArrayList;
import java.util.List;

public class Client implements Observer {
    private boolean isActive;
    private Person person;
    private List<String> messages = new ArrayList<>();


    private Client(Person person) {
        this.person = person;
        this.isActive = true;
    }

    // Factory method to create a new Client
    public static Client createClient(Person person, Object caller) throws InvalidAgeException, SecurityException, DuplicateClientException {
        if(caller.getClass() == Gym.getSecretary().getClass()){
            if(!Gym.getSecretary().equals((Secretary) caller)){
                throw new SecurityException("Error: Only the Secretary can create a Client.");
            }
        }
        else throw new SecurityException("Error: Only the Secretary can create a Client.");
        if(person.getAge() < 18){
            throw new InvalidAgeException("Error: Client is under 18 years old.");
        }
        else if(person.isRegistered()){
            throw new DuplicateClientException("Error: The client is already registered");
        }
        else{
            return new Client(person);
        }
    }

    @Override
    public void update(String message){
        this.messages.add(message);
    }

    public void changeActive(){
        this.isActive = !this.isActive;
    }
    public void receiveMessage(String message) {
        messages.add(message);
    }
    public Person getPerson() {
        return person;
    }
    public List<String> getNotifications() {
        return messages;
    }
    public boolean getActive(){
        return this.isActive;
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() == getClass()){
            Client other = (Client)obj;
            return !this.person.equals(other.person);
        }
        return false;
    }

    public String getName(){
        return this.person.getName();
    }

    @Override
    public String toString(){
        return this.person.toString();
    }
}
