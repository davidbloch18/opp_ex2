
public class Secretary {
    private Person person;
    private int salary;
    private static Secretary instance;

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

    public Client registerClient(Person p) {
        if (p.getAge() < 18) {
            throw new InvalidAgeException("Client is under 18 years old.");
        }

    }

    public void unregisterClient(Client c) {
        // Unregister the client
    }
}
