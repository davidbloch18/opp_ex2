public class Gym {
    private static Gym instance;
    private String name;
    private Secretary secretary;

    private Gym(String name) {
        this.name = name;
    }

    public static Gym getInstance(String name) {
        if (instance == null)
            instance = new Gym(name);
        return instance;
    }

    public void setSecretary(Person p, int salary) {
        secretary = Secretary.createInstance(p, salary); // Assign to the secretary field
    }

    public void setName(String name) {
        this.name = name;
    }

    public Secretary getSecretary() { // Update return type to Secretary
        return secretary;
    }
}
