package com.gmail.buer2012.resource;

import com.gmail.buer2012.entity.Request;
import com.gmail.buer2012.service.CodeRunnerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@AllArgsConstructor
public class CodeRunnerResource {
    
    private CodeRunnerService codeRunnerService;
    private static String DIRECTORYNAME = "tmp";
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/run", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> runCode(@RequestBody Request request) throws IOException {
        String className = request.getClassName();
        String pathToClass = DIRECTORYNAME + File.separator + className;
        File fileWithSourceCode = new File(pathToClass + ".java");
        
        if (fileWithSourceCode.getParentFile().mkdirs() && fileWithSourceCode.createNewFile()) {
            writeToFile(fileWithSourceCode, request.getSourceCode());
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
    
    private void writeToFile(File file, String content) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        }
    }
}
