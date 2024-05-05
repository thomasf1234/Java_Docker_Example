import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("HttpServer");

        String javaVersion = System.getProperty("java.version");
        logger.info(String.format("Running with java %s", javaVersion));

        String host = "localhost";
        String hostOverride = System.getenv("HOST");

        if (hostOverride != null) {
            host = hostOverride;
        }

        int port = 3000;

        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException ignored) { }

        try {
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            HttpServer server = HttpServer.create(socketAddress, 0);
            IMessageGateway messageGateway = new RabbitMQGateway();
            IPersonRepository personRepository = new PersonRepository();
            HttpHandler httpHandler = new MyHttpHandler(messageGateway, personRepository);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

            server.createContext("/health", httpHandler);
            server.createContext("/message/publish", httpHandler);
            server.createContext("/person", httpHandler);


            server.setExecutor(threadPoolExecutor);
            server.start();
            logger.info(String.format("Server listening on %s:%d", host, port));
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error occurred", e);
        }
    }
}