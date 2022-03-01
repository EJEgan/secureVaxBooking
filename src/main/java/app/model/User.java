package app.model;

import app.model.forum.Post;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long user_id;

    // Each user can have many posts. This is represented by the "user_id" column in the database or the "user"
    // attribute in the Post class
    @OneToMany(mappedBy="user")
    private Set<Post> posts;

    @NotBlank
    private String dob;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String ppsn;
    @NotBlank
    private String address;
    @NotBlank
    private String phone;
    @NotBlank
    private String nationality;
    @NotBlank
    private String sex;
    @NotBlank
    @Column(unique = true)
    private String email;
//    @OneToOne(cascade = CascadeType.ALL)
    @OneToOne
    @JoinColumn(name = "apt_id")
    private Appointment apt_id;

    private String dose1Date;

    private String dose2Date;

    private String lastLogin;
    @NotBlank
    private String password;
//
//    private boolean enabled;

    public User() {
        super();
    }

    public User(String dob, String name, String surname, String ppsn, String address, String phone, String nationality,String sex, String email, String password) {
        super();
        this.dob = dob;
        this.name = name;
        this.surname = surname;
        this.ppsn = ppsn;
        this.address = address;
        this.phone = phone;
        this.nationality = nationality;
        this.sex = sex;
        this.email = email;
        this.password = password;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Attributes need getters and setters
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Some attributes don't have setter methods as they should immutable once created initially (e.g. ppsn, name etc.)
    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPpsn() {
        return ppsn;
    }

    public void setPpsn(String ppsn) {
        this.ppsn = ppsn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Appointment getNextApptId() {
        return this.apt_id;
    }

    public void setNextApptId(Appointment appointment) {

        this.apt_id = appointment;
    }

    public String getDose1Date() {
        return dose1Date;
    }

    public void setDose1Date(String dose1Date) {
        this.dose1Date = dose1Date;
    }

    public String getDose2Date() {
        return dose2Date;
    }

    public void setDose2Date(String dose2Date) {
        this.dose2Date = dose2Date;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
}