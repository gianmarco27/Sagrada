package ingsw;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class Settings {
    private static final Logger log = Logger.getLogger( Settings.class.getName() );


    public static int PING_TIMEOUT;
    public static int TURN_NUMBER;
    private final static String CONFIG_PATH = "./config.properties";
    public static Integer MIN_PLAYER;
    public static Integer MAX_PLAYER;
    public static Integer TURN_TIMEOUT;
    public static String SERVER_IP;
    public static Integer TCP_PORT;
    public static Integer TCP_PORT2;
    public static Integer RMI_PORT;
    public static Boolean COLORED_OUTPUT;
    public static Integer IPSERVICE_PORT;
    public static Integer LOBBY_TIMEOUT;
    public static String LOG_LEVEL;
    public static Integer SINGLE_PLAYER_DIFFICULTY;

    static {
        Properties prop = new Properties();
        InputStream input;
        try {
            input = new FileInputStream(CONFIG_PATH);
            log.info("Loaded from file in current directory.");
        } catch (IOException ex) {
            input = Settings.class.getResourceAsStream("/Model/config.properties");
            if (input == null){
                log.warning("Could not load settings from the external file or the embedded one.");
                System.exit(1);
            }
            log.info("Loaded from the embedded resources");
        }

        try {
            prop.load(input);

            MIN_PLAYER = Integer.parseInt(prop.getProperty("min_player"));
            MAX_PLAYER = Integer.parseInt(prop.getProperty("max_player"));
            TURN_TIMEOUT = Integer.parseInt(prop.getProperty("turn_timeout"));
            TCP_PORT = Integer.parseInt(prop.getProperty("tcp_port"));
            TCP_PORT2 = Integer.parseInt(prop.getProperty("tcp_port2"));
            RMI_PORT = Integer.parseInt(prop.getProperty("rmi_port"));
            SERVER_IP = prop.getProperty("server_ip");
            TURN_NUMBER = Integer.parseInt(prop.getProperty("turn_number"));
            COLORED_OUTPUT = Boolean.parseBoolean(prop.getProperty("colored_output"));
            IPSERVICE_PORT = Integer.parseInt(prop.getProperty("ipservice_port"));
            LOBBY_TIMEOUT = Integer.parseInt(prop.getProperty("lobby_timeout"));
            SINGLE_PLAYER_DIFFICULTY = Integer.parseInt(prop.getProperty("single_player_difficulty"));
            PING_TIMEOUT = Integer.parseInt(prop.getProperty("ping_timeout"));
            LOG_LEVEL = prop.getProperty("log_level");
        } catch (IOException ex) {
            System.out.println("Error in loading properties file");
            log.warning(Arrays.toString(ex.getStackTrace()));
        }
    }
}
