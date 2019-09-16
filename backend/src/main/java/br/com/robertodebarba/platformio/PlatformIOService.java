package br.com.robertodebarba.platformio;

import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.concurrent.Executors;

@ApplicationScoped
public class PlatformIOService {

    @Inject
    private Logger logger;

    public boolean compile(String sourceCode) {
        // TODO save file on project and compile

        try {
            final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

            final ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command("cmd.exe", "/c", "platformio run");
            } else {
                builder.command("sh", "-c", "platformio run");
            }
            builder.directory(new File(System.getProperty("user.home"))); //TODO set directory
            final Process process = builder.start();

            Executors.newSingleThreadExecutor().submit(() -> this.consumeProcessOutput(process.getInputStream()));
            return this.isCompilationSuccess(process.waitFor());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void consumeProcessOutput(InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .forEach(line -> logger.info("Compiling program -> " + line));
    }

    private boolean isCompilationSuccess(int processExitCode) {
        return processExitCode == 0;
    }

}
