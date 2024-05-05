import java.sql.SQLException;

public interface IPersonRepository {
    void create(Person person) throws SQLException;
}
