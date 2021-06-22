
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * Main class for server
 *
 * @author Konanykhina Antonina
 */
public class
Main {
    public static Logger logger = LogManager.getLogger("ServerLogger");
    public static final int PORT = 1821;
    public static final int CONNECTION_TIMEOUT = 60000;


    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            Server server = new Server(PORT, CONNECTION_TIMEOUT, args[0]);
            server.run();
        } else {
            System.out.println("File path should be passed to program by using: command line argument. \n\n");
            System.exit(0);
        }

    }

}
