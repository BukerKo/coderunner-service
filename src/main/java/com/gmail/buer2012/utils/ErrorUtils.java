package com.gmail.buer2012.utils;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorUtils {
    
    public static List<String> getErrorMessages(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        return diagnostics.stream()
                .map(diagnostic -> String.format("%s on line %d in column %d:\n", diagnostic.getKind(), diagnostic.getLineNumber(), diagnostic.getColumnNumber())
                        .concat(diagnostic.getMessage(null))).collect(Collectors.toList());
    }
}
