package gym;

import gym.management.Employee.Secretary.Gym_Info;
import gym.management.Employee.Secretary.Secretary;

import java.util.HashSet;
import java.util.Set;

public class Gym {
    private static Gym instance;
    private String name;
    private static Secretary secretary;

    private Gym(String name) {
        this.name = name;
    }

    public static Gym getInstance() {
        if (instance == null) {
            instance = new Gym("Gym Name default");
        }
        return instance;
    }

    public void setSecretary(Person p, int salary) {
        if (secretary != null) {
            secretary.changeActive();
        }

        secretary = Secretary.createInstance(p, salary); // Assign to the secretary field
    }

    public static Secretary getSecretary() {
        return secretary;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Gym Name: %s\nGym Secretary: %s\nGym Balance: %d" +
                "\n" +
                "\nClients Data: %s\n" +
                "\nEmployees Data: %s\n%s\n " +
                "\nSessions Data: %s",
                this.name, secretary.toString(), secretary.getGymMoney(), secretary.get_string("Clients"),
                secretary.get_string("Instructor"), secretary.toString(), secretary.get_string("Sessions"));
    }
}
