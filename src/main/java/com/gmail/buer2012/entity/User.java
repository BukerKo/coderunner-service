package com.gmail.buer2012.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String username;

    @NonNull
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @NonNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;


    public User(String email, String password, String username, AuthProvider provider, Boolean enabled) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.provider = provider;
        this.enabled = enabled;
    }
}