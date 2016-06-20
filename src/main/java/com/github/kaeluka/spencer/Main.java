package com.github.kaeluka.spencer;
import com.github.kaeluka.spencer.server.TransformerServer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URLClassLoader;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                TransformerServer.main(new String[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        //waiting for server to start
        TransformerServer.awaitRunning();

        final Process process = getProcess(args);
        process.waitFor();

        TransformerServer.tearDown();
    }

    private static Process getProcess(final String[] args) throws IOException {
        final String sep = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + sep + "bin" + sep + "java";
        final ArrayList<String> argStrings = new ArrayList();
        final List<URL> urLs = Arrays.asList(((URLClassLoader) Main.class.getClassLoader()).getURLs());
        System.out.println("class path: "+ urLs);
        String cpPrepend =
                // the native interface class:
                ":"+System.getProperty("user.home")+("/.m2/repository/com/github/kaeluka/spencer-tracing-java/0.1.2-SNAPSHOT/spencer-tracing-java-0.1.2-SNAPSHOT.jar").replaceAll("/", sep)+
                // the transformed runtim
                ":"+System.getProperty("user.home")+("/.spencer/instrumented_java_rt/output".replaceAll("/", sep));
        argStrings.add(path);
        argStrings.add("-Xbootclasspath/p"+cpPrepend);
        argStrings.add("-agentpath:"+System.getProperty("user.home")+("/.m2/repository/com/github/kaeluka/spencer-tracing-jni/0.1.2-SNAPSHOT/spencer-tracing-jni-0.1.2-SNAPSHOT.so=tracefile=/tmp/tracefile".replaceAll("/", sep)));
        argStrings.addAll(Arrays.asList(args));
        ProcessBuilder processBuilder = new ProcessBuilder(
                argStrings);

        System.out.println("running command: "+processBuilder.command());

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        return processBuilder.start();
    }
}
