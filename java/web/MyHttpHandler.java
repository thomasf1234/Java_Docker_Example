import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class MyHttpHandler implements HttpHandler {
    private final IMessageGateway messageGateway;
    private final IPersonRepository personRepository;

    public MyHttpHandler(IMessageGateway messageGateway, IPersonRepository personRepository) {

        this.messageGateway = messageGateway;
        this.personRepository = personRepository;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue=null;
        String requestPath = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod().toString();

        switch (requestPath) {
            case "/health":
                handleResponse(httpExchange, 200,"running");
                break;
            case "/message/publish":
                Date now = new Date();
                String timestamp = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(now);

                try {
                    this.messageGateway.publish(timestamp);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
                handleResponse(httpExchange, 200, String.format("Published '%s'", timestamp));
                break;
            case "/person":
                switch (requestMethod) {
                    case "POST":
                        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(requestBody);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        String name = jsonObj.getString("name");
                        int age = jsonObj.getInt("age");
                        Person person = new Person(name, age);
                        try {
                            this.personRepository.create(person);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        handleResponse(httpExchange, 200, String.format("Created person %s aged %d", person.name, person.age));
                        break;
                    default:
                        handleResponse(httpExchange, 404, String.format("Method %s not found for %s", requestMethod, requestPath));
                        break;
                }

                break;
        }
    }

    private void handleResponse(HttpExchange httpExchange, int responseCode, String responseBody)  throws  IOException {
        java.io.OutputStream outputStream = httpExchange.getResponseBody();
        String htmlResponse = responseBody;
        httpExchange.sendResponseHeaders(responseCode, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}