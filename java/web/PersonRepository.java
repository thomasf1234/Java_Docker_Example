import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonRepository implements IPersonRepository {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PersonRepository.class);
    private final Logger logger;
    private final String connectionString;
    private Connection connection;

    public PersonRepository() throws SQLException {
        this.logger = Logger.getLogger("PersonRepository");

        String host = System.getenv("MSSQL_HOST");
        String database = System.getenv("MSSQL_DATABASE");
        String username = System.getenv("MSSQL_USERNAME");
        String password = System.getenv("MSSQL_PASSWORD");

        this.connectionString = String.format("jdbc:sqlserver://%s:1433;databaseName=%s;user=%s;password=%s;encrypt=false", host, database, username, password);

        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
    }

    public void create(Person person) throws SQLException {
        ensureConnection();

        Statement statement = this.connection.createStatement();
        String createPersonSql =  String.format("INSERT INTO [dbo].[Person] (Name, Age)" +
                " VALUES ('%s', %d);", person.name, person.age);

        int insertCount = statement.executeUpdate(createPersonSql);

        if (insertCount != 1) {
            throw new SQLException(String.format("Error occurred inserting person {Name: %s, Age: %d}.", person.name, person.age));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.connection != null && !this.connection.isClosed()) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                this.logger.log(Level.SEVERE, "Error occurred closing MSSQL connection", e);
            }
        }
    }
    private void ensureConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = DriverManager.getConnection(this.connectionString);

            if (this.connection == null) {
                throw new SQLException("MSSQL connection null");
            } else {
                this.logger.info("Connected to MSSQL");
            }
        }

        Statement statement = this.connection.createStatement();
        String createTableSql =  "CREATE TABLE Person " +
                "(PersonId INTEGER IDENTITY(1, 1) NOT NULL PRIMARY KEY, " +
                " Name VARCHAR(255), " +
                " Age INTEGER);";

        try {
            statement.executeUpdate(createTableSql);
        } catch (SQLServerException sse) {
            if (sse.getSQLServerError().getErrorNumber() != 2714) {
                throw sse;
            }
        }
    }
}
