package devalrykemes.exchangeconverterapp.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DBRepository {

    private static Connection connection;
    private Statement statement;
    private String jdbcURL = "jdbc:h2:mem:testdb";

    public H2DBRepository() {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, "sa", "");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    private void createTableConversionRecords() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS conversionRecords (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "currencyConvert VARCHAR(20)," +
                "currencyConverted VARCHAR(20));";

        statement.execute(createTableSQL);
    }
}