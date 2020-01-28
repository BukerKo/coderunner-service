package com.gmail.buer2012.payload;

import lombok.Data;

import java.util.List;

@Data
public class CodeRunnerResponse {
    private List<String> output;
    private List<String> errors;
    private Integer numberOfTries;
}
