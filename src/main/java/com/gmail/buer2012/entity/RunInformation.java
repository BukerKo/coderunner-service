package com.gmail.buer2012.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class RunInformation {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    private Integer numberOfTries;
    
    private String pathToLastAttempt;
    
    @OneToOne
    private User user;
    
    public RunInformation(String pathToLastAttempt, Integer numberOfTries, User user) {
        this.pathToLastAttempt = pathToLastAttempt;
        this.numberOfTries = numberOfTries;
        this.user = user;
    }
    
}
