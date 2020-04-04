package com.gmail.buer2012.service;

import static com.gmail.buer2012.utils.ErrorUtils.getErrorMessages;

import com.gmail.buer2012.config.CustomProperties;
import com.gmail.buer2012.entity.RunInformation;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.payload.CoderunnerRequest;
import com.gmail.buer2012.repository.FeatureRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
@AllArgsConstructor
public class CodeRunnerService {
    
    private FeatureRepository featureRepository;
    private RunInformationService runInformationService;
    private FileStorageService fileStorageService;
    
    private final CustomProperties customProperties;
    private static final String gatherInformation = "gatherInformation";
    
    private static final String DOCKERFILENAME = "Dockerfile";
    private static final Integer EXECUTION_TIMEOUT_SECONDS = 10;
    
    public Map<String, List<String>> compileAndRun(CoderunnerRequest coderunnerRequest, User user)
        throws IOException, InterruptedException {
        String className = coderunnerRequest.getClassName();
        File fileWithSourceCode = new File(getPathToClass() + File.separator + className + ".java");
        
        if (fileWithSourceCode.getParentFile().mkdirs() && fileWithSourceCode.createNewFile()) {
            writeToFile(fileWithSourceCode, coderunnerRequest.getSourceCode());
            Map<String, List<String>> compileErrors = compile(fileWithSourceCode);
            if (compileErrors != null) {
                executePostRunActions(user, fileWithSourceCode);
                return compileErrors;
            }
        }

        Map<String, List<String>> result = run(fileWithSourceCode, className);
        executePostRunActions(user, fileWithSourceCode);
        return result;
    }
    
    private void executePostRunActions(User user, File fileWithSourceCode)
        throws IOException {
        Boolean gatherInformationEnabled = featureRepository.findByFeatureName(gatherInformation).getEnabled();
        if (gatherInformationEnabled) {
            Optional<RunInformation> runInformation = runInformationService
                .getByUser(user);
            runInformation.ifPresent(information -> fileStorageService
                .deleteFile(information.getPathToLastAttempt()));
            runInformationService
                .saveOrUpdate(fileStorageService.storeFile(fileWithSourceCode),
                    user);
        }
        FileSystemUtils.deleteRecursively(fileWithSourceCode.getParentFile());
    }
    
    private String getPathToClass() {
        return customProperties.getTemporaryDir() + File.separator + System.currentTimeMillis();
    }
    
    
    public Map<String, List<String>> compile(File fileWithSourceCode) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        Iterable<? extends JavaFileObject> compilationUnit
                = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(fileWithSourceCode));
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                null,
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
    
    public Map<String, List<String>> run(File fileWithSourceCode, String className)
        throws IOException, InterruptedException {
        createDockerfile(className, fileWithSourceCode.getParent());
        
        String dockerContainerName = String.valueOf(System.currentTimeMillis());
        String dockerBuildCommand = "docker build -t " + dockerContainerName + " .";
        String dockerRunCommand = "docker run " + dockerContainerName;
        
        Process buildDocker = Runtime.getRuntime().exec(dockerBuildCommand, null, fileWithSourceCode.getParentFile());
        buildDocker.waitFor();

        final Process runDocker = Runtime.getRuntime().exec(dockerRunCommand, null, fileWithSourceCode.getParentFile());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Map<String, List<String>>> output = executor.submit(() -> {
            Map<String, List<String>> result = new HashMap<>();
            result.put("errors", parseOutputFromProgram(runDocker.getErrorStream()));
            result.put("output", parseOutputFromProgram(runDocker.getInputStream()));
            return result;
        });

        try {
            return output.get(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            output.cancel(true);
            Map<String, List<String>> result = new HashMap<>();
            if(e instanceof TimeoutException) {
                result.put("errors", Collections.singletonList("Timeout running your code"));
            }
            else {
                result.put("errors", Collections.singletonList(e.getMessage()));
            }
            return result;
        }
    }
    
    private void createDockerfile(String className, String directoryToSaveTo) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(directoryToSaveTo + File.separator + DOCKERFILENAME));
        writer.write("FROM adoptopenjdk:11-jre-hotspot\n");
        writer.write("COPY " + className + ".class /\n");
        writer.write("CMD [\"java\", \"" + className + "\"]");
        writer.close();
    }
    
    private List<String> parseOutputFromProgram(InputStream inputStream) {
        BufferedReader errors = new BufferedReader(new InputStreamReader(inputStream));
        return Collections.singletonList(errors.lines()
            .collect(Collectors.joining(System.lineSeparator())));
    }
    
    private void writeToFile(File file, String content) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        }
    }
}
