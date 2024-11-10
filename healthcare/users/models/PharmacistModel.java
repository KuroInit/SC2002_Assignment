package healthcare.users.models;

public class PharmacistModel {
    private String pharmacistID;
    private String name;
    private String gender;
    private String age;

    public PharmacistModel(String pharmacistID, String name, String gender, String age) {
        this.pharmacistID = pharmacistID;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getPharmacistID() {
        return pharmacistID;
    }

    public void setPharmacistID(String pharmacistID) {
        this.pharmacistID = pharmacistID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
