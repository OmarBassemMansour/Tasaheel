package Graduation.Project.Tasaheel.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "`user`" , uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String nationalId;
    private String country;
    private String city;
    private String password;
    private String phoneNb;
    private String gender;
    private double rate;
    @Nullable
    private boolean isBanned;
    private LocalDateTime banExpiration;
    private acceptedByAdmin acceptedByAdmin;
    @Column(length = 10485760) // Increase the length to accommodate the Base64 encoded string
    private String profileImage;
    @Column(length = 10485760) // Increase the length to accommodate the Base64 encoded string
    private String frontId;
    @Column(length = 10485760) // Increase the length to accommodate the Base64 encoded string
    private String backId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "user_interest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<Interest> interests = new HashSet<>();

    @ElementCollection
    private List<Double> rates = new ArrayList<>();

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFrontId() {
        return frontId;
    }

    public void setFrontId(String frontId) {
        this.frontId = frontId;
    }

    public String getBackId() {
        return backId;
    }

    public void setBackId(String backId) {
        this.backId = backId;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public LocalDateTime getBanExpiration() {
        return banExpiration;
    }

    public void setBanExpiration(LocalDateTime banExpiration) {
        this.banExpiration = banExpiration;
    }
    // Getters and setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getNationalId() { return nationalId; }

    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPhoneNb() { return phoneNb; }

    public void setPhoneNb(String phoneNb) { this.phoneNb = phoneNb; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public double getRate() { return rate; }

    public void setRate(double rate) { this.rate = rate; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public Set<Interest> getInterests() { return interests; }

    public void setInterests(Set<Interest> interests) { this.interests = interests; }

    public List<Double> getRates() { return rates; }

    public void setRates(List<Double> rates) { this.rates = rates; }

    public Graduation.Project.Tasaheel.models.acceptedByAdmin getAcceptedByAdmin() {   return acceptedByAdmin;}

    public void setAcceptedByAdmin( acceptedByAdmin acceptedByAdmin) {   this.acceptedByAdmin = acceptedByAdmin;}

    public void addRate(double rate) {
        this.rates.add(rate);
        calculateAverageRate();
    }

    private void calculateAverageRate() {
        double total = 0;
        for (double r : rates) {
            total += r;
        }
        this.rate = rates.isEmpty() ? 0 : total / rates.size();
    }
}
