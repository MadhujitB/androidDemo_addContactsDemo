package demo.dada.com.addcontactsdemo;

/**
 * Created by DADA on 05-03-2018.
 */

public class RegistrationData {

    private int id;
    private String name;
    private long phone;
    private String email;
    private String city;
    private String state;
    private String password;

    public RegistrationData(int id,
            String name,
            long phone,
            String email,
            String city,
            String state,
            String password)
    {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.state = state;
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPassword() {
        return password;
    }
}
