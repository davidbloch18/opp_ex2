package gym;

import java.util.Objects;

public class Person {
    private String name;
    private Gender gender;
    private String date_of_birth;
    private int balance;
    private int ID;
    private static int objectCount = 0;
    private boolean isClient;

    public Person(String name, int balance, Gender gender, String date_of_birth){
        objectCount +=1;
        this.name = name;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.balance = balance;
        this.ID = objectCount;
        this.isClient = false;
    }

    public int getAge(){
        String[] parts = date_of_birth.split("-");
        int year = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[0]);

        java.time.LocalDate birthDate = java.time.LocalDate.of(year, month, day);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.Period age = java.time.Period.between(birthDate, currentDate);

        return age.getYears();
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass() == getClass()){
            Person other = (Person)obj;
            if((Objects.equals(this.name, other.getName())) && (Objects.equals(this.date_of_birth, other.getDateOfBirth()))
                    && (other.getGender() == this.gender) && (this.ID == other.getID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return String.format("ID: %s | Name: %s | Gender: %s | Birthday: %s | Age: %d | Balance: %d",
                this.ID, this.name, this.gender, this.date_of_birth, this.getAge(), this.balance);
    }

    public boolean isRegistered(){
        return this.isClient;
    }
    public void unregister(){
            this.isClient = false;
    }
    public void register(){
            this.isClient = true;
     }
    public int getID(){
            return this.ID;
    }
    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return date_of_birth;
    }
    public int getBalance(){return this.balance; }
    public void changeBalance(int amount){
        this.balance += amount;
    }

}
