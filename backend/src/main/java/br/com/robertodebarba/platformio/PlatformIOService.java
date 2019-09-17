package br.com.robertodebarba.platformio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.concurrent.Executors;

@ApplicationScoped
public class PlatformIOService {

    private Logger logger = LoggerFactory.getLogger(PlatformIOService.class);

    public boolean compile(String sourceCode, String projectDirectory) {
        try {
            final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

            logger.info("Compiling program -> " + projectDirectory);

            final String sourceCodeFilePath = projectDirectory + File.separator + "src" + File.separator + "main.cpp";
            this.saveSourceCodeToFile(sourceCode, sourceCodeFilePath);

            final ProcessBuilder builder = new ProcessBuilder();
            if (isWindows) {
                builder.command("cmd.exe", "/c", "platformio run");
            } else {
                builder.command("sh", "-c", "platformio run");
            }
            builder.directory(new File(projectDirectory));
            final Process process = builder.start();

            Executors.newSingleThreadExecutor().submit(() -> this.consumeProcessOutput(process.getInputStream()));

            return this.isCompilationSuccess(process.waitFor());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run compile command", e);
        }
    }

    private void saveSourceCodeToFile(String sourceCode, String filePath) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(sourceCode);
        writer.close();
    }

    private void consumeProcessOutput(InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .forEach(line -> logger.info("Compiling program -> " + line));
    }

    private boolean isCompilationSuccess(int processExitCode) {
        if (processExitCode == 0) {
            logger.info("Compiling program -> SUCCESS");
            return true;
        }

        logger.info("Compiling program -> FAIL");
        return false;
    }

}
