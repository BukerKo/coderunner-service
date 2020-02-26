package com.gmail.buer2012.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
public class EmailConfirmationToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @NonNull
    private User user;
    
    @NonNull
    private String token;
    
    public EmailConfirmationToken(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
