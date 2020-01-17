package com.gmail.buer2012.controller;

import com.gmail.buer2012.config.CustomProperties;
import com.gmail.buer2012.payload.CoderunnerRequest;
import com.gmail.buer2012.payload.SendEmailRequest;
import com.gmail.buer2012.security.CurrentUser;
import com.gmail.buer2012.security.UserPrincipal;
import com.gmail.buer2012.service.CodeRunnerService;
import com.gmail.buer2012.service.EmailSenderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@AllArgsConstructor
public class CodeRunnerController {
    
    private CodeRunnerService codeRunnerService;
    private EmailSenderService emailSenderService;
    
    private final CustomProperties customProperties;
    
    @PostMapping(value = "/run", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> runCode(@RequestBody CoderunnerRequest coderunnerRequest) throws IOException, InterruptedException {
        String className = coderunnerRequest.getClassName();
        String pathToClass = customProperties.getTemporaryDir() + File.separator + className;
        File fileWithSourceCode = new File(pathToClass + ".java");
        
        if (fileWithSourceCode.getParentFile().mkdirs() && fileWithSourceCode.createNewFile()) {
            writeToFile(fileWithSourceCode, coderunnerRequest.getSourceCode());
            Map<String, List<String>> errors = codeRunnerService.compile(fileWithSourceCode);
            if (errors != null) {
                FileSystemUtils.deleteRecursively(fileWithSourceCode.getParentFile());
                return ResponseEntity.ok(errors);
            }
        }
        
        Map<String, List<String>> result = codeRunnerService.run(fileWithSourceCode, className);
        FileSystemUtils.deleteRecursively(fileWithSourceCode.getParentFile());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping(value = "/sendCode", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> sendCode(@RequestBody SendEmailRequest sendEmailRequest, @CurrentUser UserPrincipal userPrincipal) {
        emailSenderService.sendEmail(userPrincipal.getEmail(), sendEmailRequest.getCode());
        Map<String, String> res = new HashMap<>();
        res.put("status", "success");
        return ResponseEntity.ok().body(res);
    }
    
    private void writeToFile(File file, String content) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        }
    }
}
