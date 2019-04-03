package ingsw.Server.Utility;

import ingsw.Settings;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtils {
    public static void initLoggerSettings(){

        Level level = Level.parse(Settings.LOG_LEVEL);
        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            rootLogger.setLevel(level);
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler h : handlers) {
                h.setLevel(level);
            }

        } catch (NullPointerException | SecurityException e) {
            System.err.println("Failed to init logger");
            System.exit(1);
        }
    }
    }


