package org.shopme.common.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    @Column(length = 100, nullable = false)
    private String password;
    @Column(name = "first_name", length = 45, nullable = false)
    private String firstname;
    @Column(name = "last_name", length = 45, nullable = false)
    private String lastname;
    @Column(length = 64)
    private String photo;
    private boolean enabled;
    private Timestamp joined = new Timestamp(System.currentTimeMillis());

    public User(String email, String password, String firstname, String lastname, String photo, boolean enabled, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.photo = photo;
        this.enabled = enabled;
        this.roles = roles;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        getRoles().add(role);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", firstname=" + firstname + ", lastname=" + lastname
                + ", enabled=" + enabled + "]";
    }
}
