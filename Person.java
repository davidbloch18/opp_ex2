public class Person {
    private String name;
    private int money_left;

    private Gender gender; // Declare the field
    private String birth_date;

    public Person(String name, int money_left, Gender gender, String birth_date) {
        this.name = name;
        this.money_left = money_left;
        this.gender = gender; // Assign the parameter to the field
        this.birth_date = birth_date;
    }

    protected String getName() {
        return name;
    }

    protected int getMoneyLeft() {
        return money_left;
    }

    protected Gender getGender() {
        return gender;
    }

    protected String getBirthDate() {
        return birth_date;
    }

    protected boolean isAdult() {

        String[] parts = birth_date.split("-");
        int year = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[0]);

        java.time.LocalDate birthDate = java.time.LocalDate.of(year, month, day);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.Period age = java.time.Period.between(birthDate, currentDate);

        return age.getYears() >= 18;

    }

    public void setMoneyLeft(int money_left) {
        this.money_left = money_left;
    }

    protected boolean isElderly() {
        String[] parts = birth_date.split("-");
        int year = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[0]);

        java.time.LocalDate birthDate = java.time.LocalDate.of(year, month, day);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.Period age = java.time.Period.between(birthDate, currentDate);

        return age.getYears() >= 65;
    }
}
