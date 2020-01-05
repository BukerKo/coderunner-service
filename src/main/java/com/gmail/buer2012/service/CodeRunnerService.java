package com.gmail.buer2012.service;


import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.util.*;

import static com.gmail.buer2012.utils.ErrorUtils.getErrorMessages;

@Service
public class CodeRunnerService {

    @Setter
    private String className;

    private static final String CLASSPATH = System.getProperty("java.class.path");

    public Map<String, List<String>> compile(String fileWithSourceCode) throws IOException {

        Class<?> aClass = SimpleJavaCompiler.compile(Object.class, fileWithSourceCode);

        Map<String, List<String>> run = run(aClass);

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
    
    public Map<String, List<String>> run(File fileWithSourceCode, String className) throws IOException, InterruptedException {
        createDockerfile(className);
        
        String dockerContainerName = String.valueOf(System.currentTimeMillis());
        String dockerBuildCommand = "docker build -t " + dockerContainerName + " .";
        String dockerRunCommand = "docker run " + dockerContainerName;
        
        Process buildDocker = Runtime.getRuntime().exec(dockerBuildCommand, null, new File(customProperties.getTemporaryDir()));
        buildDocker.waitFor();
        Process runDocker = Runtime.getRuntime().exec(dockerRunCommand, null, new File(customProperties.getTemporaryDir()));
    
        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", parseOutputFromProgram(runDocker.getErrorStream()));
        result.put("output", parseOutputFromProgram(runDocker.getInputStream()));
        
        return result;
    }
    
    private void createDockerfile(String className) throws IOException {
        String directoryToSaveTo = customProperties.getTemporaryDir() + File.separator;
        BufferedWriter writer = new BufferedWriter(new FileWriter(directoryToSaveTo + DOCKERFILENAME));
        writer.write("FROM openjdk:8-jre-alpine\n");
        writer.write("COPY " + className + ".class /\n");
        writer.write("CMD [\"java\", \"" + className + "\"]");
        writer.close();
    }
    
    private List<String> parseOutputFromProgram(InputStream inputStream) throws IOException {
        BufferedReader errors = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder stringBuffer = new StringBuilder();
        while ((line = errors.readLine()) != null) {
            stringBuffer.append(line);
        }
        return Collections.singletonList(stringBuffer.toString());
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

