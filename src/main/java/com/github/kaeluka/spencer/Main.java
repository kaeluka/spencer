package com.github.kaeluka.spencer;
import com.github.kaeluka.spencer.server.TransformerServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.io.File;

import org.apache.commons.io.IOUtils;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                TransformerServer.main(new String[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("waiting for server to start");
        Thread.currentThread().sleep(3000);
        System.out.println("waiting for server to start: done");


        String sep = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + sep + "bin" + sep + "java";
        final String argString = StringUtils.join(Arrays.asList(args), " ");
//        final List<URL> urLs = Arrays.asList(((URLClassLoader) Main.class.getClassLoader()).getURLs());
//        System.out.println("class path: "+ urLs);
        //FIXME hard coded paths
        ProcessBuilder processBuilder = new ProcessBuilder(
                path,
                "-Xbootclasspath/p:/Users/stebr742/.m2/repository/com/github/kaeluka/spencer-tracing-java/0.1.2-SNAPSHOT/spencer-tracing-java-0.1.2-SNAPSHOT.jar",
                "-agentpath:/Users/stebr742/.m2/repository/com/github/kaeluka/spencer-tracing-jni/0.1.2-SNAPSHOT/spencer-tracing-jni-0.1.2-SNAPSHOT.so=tracefile=/tmp/tracefile",
                argString);

        System.out.println(processBuilder.command());

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        final Process process = processBuilder.start();
        process.waitFor();

        TransformerServer.tearDown();
    }
}
