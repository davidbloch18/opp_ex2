import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final List<Client> activeClients = new ArrayList<>();
    private boolean isActive;
    private Person person;
    private List<String> messages = new ArrayList<>();

    // Private constructor to restrict instantiation
    private Client(Person person, boolean isActive) {
        this.person = person;
        this.isActive = isActive;
    }

    // Factory method to create a new Client
    public static Client createClient(Person person) {
        if (!isCallerSecretary()) {
            throw new SecurityException("Only the Secretary can create a Client.");
        }
        if (!person.isAdult()) {
            throw new InvalidAgeException("Client is under 18 years old.");
        }
        Client client = new Client(person, true);
        activeClients.add(client);
        return client;
    }

    // Method to remove a Client
    public static void removeClient(Client client) {
        if (!isCallerSecretary()) {
            throw new SecurityException("Only the Secretary can remove a Client.");
        }
        activeClients.remove(client);
        client.isActive = false;
    }

    // Check if a Client is active
    public boolean isActive() {
        return isActive;
    }

    // Get all active clients
    public static List<Client> getActiveClients() {
        return new ArrayList<>(activeClients); // Return a copy for safety
    }

    // Helper method to verify the caller
    private static boolean isCallerSecretary() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            String callerClassName = stackTrace[3].getClassName();
            return "com.company.hr.Secretary".equals(callerClassName);
        }
        return false;
    }

    // Getters
    public Person getPerson() {
        return person;
    }

    public String getName() {
        return person.getName();

    }

    @Override
    public void receiveMessage(String message) {
        messages.add(message);
    }

    public List<String> getNotifications() {
        return messages;
    }
}
