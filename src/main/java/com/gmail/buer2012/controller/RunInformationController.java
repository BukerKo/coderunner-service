package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.RunInformation;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.security.CurrentUser;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.RunInformationService;
import com.gmail.buer2012.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RunInformationController {
    
    private RunInformationService runInformationService;
    private UserService userService;
    
    @GetMapping(value = "/runInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getRunInfo(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId()).get();
        RunInformation runInformation = runInformationService.getByUser(user).get();
        return ResponseEntity.ok(runInformation.getNumberOfTries());
    }

}
