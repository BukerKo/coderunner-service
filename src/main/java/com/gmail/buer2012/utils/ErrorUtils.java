package com.gmail.buer2012.utils;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Collections;
import java.util.List;

public class ErrorUtils {
    
    public static List<String> getErrorMessages(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        Diagnostic<? extends JavaFileObject> diagnostic = diagnostics.get(diagnostics.size() -1 );
        return Collections.singletonList(String.format("%s on line %d in column %d:\n", diagnostic.getKind(), diagnostic.getLineNumber(), diagnostic.getColumnNumber())
                .concat(diagnostic.getMessage(null)));
    }
}
