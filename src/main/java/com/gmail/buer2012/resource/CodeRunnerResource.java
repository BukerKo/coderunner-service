package com.gmail.buer2012.resource;

import com.gmail.buer2012.entity.Request;
import com.gmail.buer2012.service.CodeRunnerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@AllArgsConstructor
public class CodeRunnerResource {

    private CodeRunnerService codeRunnerService;

    @PostMapping(value = "/run", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> runCode(@RequestBody Request request) throws IOException {

        codeRunnerService.setClassName(request.getClassName());

        Map<String, List<String>> compileErrors = codeRunnerService.compile(request.getSourceCode());
        if (compileErrors != null) {
            return ResponseEntity.ok(compileErrors)
        }

//        Map<String, List<String>> result = codeRunnerService.run();
        return ResponseEntity.ok(null);
    }
}
