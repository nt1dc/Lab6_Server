import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;


/**
 * Main class for server
 *
 * @author Konanykhina Antonina
 */
public class
Main {
    public static Logger logger = LogManager.getLogger("ServerLogger");
    public static final int PORT = 1048;
    public static final int CONNECTION_TIMEOUT = 60000;


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(InetAddress.getLocalHost());
        Server server = new Server(PORT, CONNECTION_TIMEOUT);
        server.run();

    }

}
