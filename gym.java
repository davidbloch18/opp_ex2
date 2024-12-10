public class Gym {
    private static Gym instance;
    private String name;

    private Gym(String name) {
        this.name = name;
    }

    public static Gym getInstance(String name) {
        if (instance == null)
            instance = new Gym(name);
        return instance;
    }

    public void setSecretary(Person p, int salary) {
        Secretary secretary = Secretary.createInstance(p, salary);

    }

    public void setName(String name) {
        this.name = name;
    }

    // // // getters for secretary and salary if needed
    // // public Person getSecretary() {
    // return secretary;
    // // }

    // // public int getSalary() {
    // return salary;
    // // }
}
