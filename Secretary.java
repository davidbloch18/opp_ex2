import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Secretary {
    private Person person;
    private int salary;
    private static Secretary instance;
    private List<Instructor> hired_instructors;

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

    public Client registerClient(Person person) {
        return Client.createClient(person);
    }

    public void unRegisterClient(Client client) {
        Client.removeClient(client);
    }

    public List<Client> getActiveClients() {
        return Client.getActiveClients();
    }

    // Method to hire an instructor
    public Instructor hireInstructor(Person person, int hourlyRate, List<Session> admittedSessions) {
        Instructor newInstructor = Instructor.createInstructor(person, hourlyRate, admittedSessions);
        hired_instructors.add(newInstructor);
        return newInstructor;
    }

    // Method to find an instructor by their admitted sessions
    public Optional<Instructor> findInstructorByAdmittedSessions(List<Session> admittedSessions) {
        return hired_instructors.stream()
                .filter(instructor -> instructor.getAdmittedSessions().containsAll(admittedSessions))
                .findFirst();
    }

    // Method to create a session
    public Session createSession(String type, String date, String time, Instructor instructor) {
        Session session = Session.createSession(type, date, time, instructor);
        instructor.getAdmittedSessions().add(session);
        return session;
    }
}
