package com.gmail.buer2012.service;


import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.gmail.buer2012.utils.ErrorUtils.getErrorMessages;

@Service
public class CodeRunnerService {
    
    public Map<String, List<String>> compile(File fileWithSourceCode) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    
        List<String> optionList = new ArrayList<>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path"));
    
        Iterable<? extends JavaFileObject> compilationUnit
                = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(fileWithSourceCode));
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                optionList,
                null,
                compilationUnit);
    
        if (!task.call()) {
            return handleErrors(diagnostics.getDiagnostics());
        }
        fileManager.close();
    
        return null;
    }
    
    private Map<String, List<String>> handleErrors(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        Map<String, List<String>> result = new HashMap<>();
        if (diagnostics != null) {
            result.put("errors", getErrorMessages(diagnostics));
            return result;
        }
        return result;
    }
    
    public Map<String, List<String>> run(File fileWithSourceCode, String className) throws IOException {
        Map<String, List<String>> result = new HashMap<>();
        Process process = Runtime.getRuntime().exec("java " + className, null, new File("tmp"));
        BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        StringBuilder stringBuffer = new StringBuilder();
        while ((line = errors.readLine()) != null) {
            stringBuffer.append(line);
        }
        result.put("errors", Collections.singletonList(stringBuffer.toString()));
    
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        result.put("output", Collections.singletonList(in.readLine()));
        return result;
    }
}
