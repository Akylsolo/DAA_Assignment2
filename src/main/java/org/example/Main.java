package org.example;

import org.example.benchmark.MainBenchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (System.getProperty("app.restarted") == null) {
            try {
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
                String classPath = System.getProperty("java.class.path");

                List<String> command = new ArrayList<>();
                command.add(javaBin);
                command.add("--sun-misc-unsafe-memory-access=allow");
                command.add("-Djdk.attach.allowAttachSelf=true");
                command.add("-Dapp.restarted=true");
                command.add("-cp");
                command.add(classPath);
                command.add("org.example.Main");
                for (String arg : args) {
                    command.add(arg);
                }

                ProcessBuilder pb = new ProcessBuilder(command);
                pb.inheritIO();
                Process process = pb.start();
                int exitCode = process.waitFor();
                System.exit(exitCode);
                return;
            } catch (Exception ignored) {
            }
        }

        MainBenchmark.main(args);
    }
}
