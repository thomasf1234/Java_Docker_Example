import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IMessageGateway {
    void publish(String message) throws IOException, TimeoutException;
}
