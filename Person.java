public class Person {
    private String name;
    private int money_left;

    private enum Gender {
        male,
        female
    }

    private Gender gender; // Declare the field
    private String birth_date;

    public Person(String name, int money_left, Gender gender, String birth_date) {
        this.name = name;
        this.money_left = money_left;
        this.gender = gender; // Assign the parameter to the field
        this.birth_date = birth_date;
    }
}
