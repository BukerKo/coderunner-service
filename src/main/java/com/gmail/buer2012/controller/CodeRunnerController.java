package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.payload.CodeRunnerResponse;
import com.gmail.buer2012.payload.CoderunnerRequest;
import com.gmail.buer2012.payload.SendEmailRequest;
import com.gmail.buer2012.security.CurrentUser;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.CodeRunnerService;
import com.gmail.buer2012.service.EmailSenderService;
import com.gmail.buer2012.service.RunInformationService;
import com.gmail.buer2012.service.UserService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CodeRunnerController {
    
    private CodeRunnerService codeRunnerService;
    private UserService userService;
    private EmailSenderService emailSenderService;
    private RunInformationService runInformationService;
    
    
    @PostMapping(value = "/run", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CodeRunnerResponse> runCode(@RequestBody CoderunnerRequest coderunnerRequest,
                                                      @CurrentUser UserPrincipal userPrincipal)
        throws IOException, InterruptedException {
        User user = userService.findById(userPrincipal.getId()).get();
        Map<String, List<String>> result = codeRunnerService.compileAndRun(coderunnerRequest, user);
        CodeRunnerResponse response = new CodeRunnerResponse();
        response.setErrors(result.get("errors"));
        response.setOutput(result.get("output"));
        runInformationService.getByUser(user).ifPresent(runInformation -> response.setNumberOfTries(runInformation.getNumberOfTries()));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/sendCode", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> sendCode(@RequestBody SendEmailRequest sendEmailRequest, @CurrentUser UserPrincipal userPrincipal) {
        emailSenderService.sendEmail(userPrincipal.getEmail(), sendEmailRequest.getCode(), "Your code from CodeRunner");
        Map<String, String> res = new HashMap<>();
        res.put("status", "success");
        return ResponseEntity.ok().body(res);
    }
    
    
}
