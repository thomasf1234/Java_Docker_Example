import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class RabbitMQGateway implements IMessageGateway {
    private final Logger logger;
    private final String queue;
    private final ConnectionFactory connectionFactory;
    private Connection connection;

    public RabbitMQGateway() throws IOException, TimeoutException {
        this.logger = Logger.getLogger("MessageService");
        this.queue = System.getenv("RABBITMQ_QUEUE");

        String host = System.getenv("RABBITMQ_HOST");
        String username = System.getenv("RABBITMQ_USERNAME");
        String password = System.getenv("RABBITMQ_PASSWORD");

        this.connectionFactory = new ConnectionFactory();
        this.connectionFactory.setHost(host);
        this.connectionFactory.setUsername(username);
        this.connectionFactory.setPassword(password);
    }

    public void publish(String message) throws IOException, TimeoutException {
        if (this.connection == null) {
            this.connection = connectionFactory.newConnection();
        }

        Channel channel = connection.createChannel();
        channel.queueDeclare(this.queue, false, false, false, null);
        channel.basicPublish("", this.queue, null, message.getBytes("UTF-8"));
        this.logger.info(String.format("Published message {%s} to queue {%s}", message, this.queue));
    }
}
