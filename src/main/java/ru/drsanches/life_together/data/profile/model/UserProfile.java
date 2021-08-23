package ru.drsanches.life_together.data.profile.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user_profile")
public class UserProfile {

    @Id
    @Column
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private String firstName;

    @Column
    private String lastName;

    public UserProfile() {}

    public UserProfile(String id, String username, boolean enabled, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}