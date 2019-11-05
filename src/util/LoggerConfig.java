package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static util.ErrorCodes.LOGGER_PROBLEM;

/**
 * @author Ian Laird
 * For setting up a logger
 */
public class LoggerConfig {

    /**
     * log
     * @param fileName the name of the file
     * @return configures the log
     */
    public static Logger setupLogger(Logger logger, String fileName){
        // do not log to the console
        logger.setUseParentHandlers(false);

        // setup the logger
        try {
            FileHandler logFile = new FileHandler(fileName);
            SimpleFormatter formatter = new SimpleFormatter();
            logFile.setFormatter(formatter);
            logger.addHandler(logFile);
        }catch(IOException e){
            System.err.println("Error: Unable to create fileHandler for " + fileName);
            System.exit(LOGGER_PROBLEM);
        }
        return logger;
    }
}
