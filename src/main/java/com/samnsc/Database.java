package com.samnsc;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Database {
    private static final String connectionUrl = "jdbc:sqlite:market.db";
    private static final Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    static {
        Connection auxConnection;
        try {
            SQLiteConfig databaseConfiguration = new SQLiteConfig();
            databaseConfiguration.enforceForeignKeys(true);
            auxConnection = DriverManager.getConnection(connectionUrl, databaseConfiguration.toProperties());
        } catch (SQLException exception) {
            auxConnection = null;
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to connect to database", exception);
        }
        connection = auxConnection;
    }

    public static void createDatabase() {
        try {
            Statement statement = connection.createStatement();

            String userTable = "CREATE TABLE IF NOT EXISTS \"user\"(" +
                    "\"id\"                 INTEGER     NOT NULL UNIQUE," +
                    "\"name\"               TEXT        NOT NULL," +
                    "\"identification\"     TEXT        NOT NULL UNIQUE," +
                    "\"email\"              TEXT        UNIQUE," +
                    "\"phone_number\"       TEXT        UNIQUE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)" +
                    ");";
            statement.addBatch(userTable);

            String clientTable = "CREATE TABLE IF NOT EXISTS \"client\"(" +
                    "\"user_id\"            INTEGER     NOT NULL UNIQUE," +
                    "\"affiliation_date\"   DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "PRIMARY KEY(\"user_id\")," +
                    "FOREIGN KEY(\"user_id\") REFERENCES \"user\"(\"id\")" +
                    ");";
            statement.addBatch(clientTable);

            String workerTable = "CREATE TABLE IF NOT EXISTS \"worker\"(" +
                    "\"user_id\"        INTEGER     NOT NULL UNIQUE," +
                    "\"username\"       TEXT        NOT NULL UNIQUE," +
                    "\"password\"       TEXT        NOT NULL," +
                    "\"worker_type\"    TEXT        NOT NULL CHECK(\"worker_type\" IN ('CASHIER', 'MANAGER'))," +
                    "\"start_date\"     DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "\"end_date\"       DATE," +
                    "PRIMARY KEY(\"user_id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"user_id\") REFERENCES \"user\"(\"id\")," +
                    "CHECK (end_date IS NULL OR start_date <= end_date)" +
                    ");";
            statement.addBatch(workerTable);

            String productTable = "CREATE TABLE IF NOT EXISTS \"product\" (" +
                    "\"id\"                     INTEGER     NOT NULL UNIQUE," +
                    "\"name\"                   TEXT        NOT NULL," +
                    "\"product_code\"           TEXT        NOT NULL UNIQUE," + // codificação seguindo o padrão EAN-13 (3 primeiros números são o código do país, tamanho variável do código da marca, tamanho variável do código do produto e um check-digit)
                    "\"measurement_type\"       TEXT        NOT NULL CHECK(\"measurement_type\" IN ('UNIT', 'KILOGRAM'))," +
                    "\"selling_price\"          DOUBLE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)" +
                    ");";
            statement.addBatch(productTable);

            String purchaseTable = "CREATE TABLE IF NOT EXISTS \"purchase\" (" +
                    "\"id\"                 INTEGER     NOT NULL UNIQUE," +
                    "\"purchase_price\"     DOUBLE      NOT NULL," +
                    "\"payment_method\"     TEXT        CHECK(\"payment_method\" IN ('MONEY', 'CREDIT_CARD', 'DEBIT_CARD', 'CHECK', 'PIX')) NOT NULL," +
                    "\"cashier_id\"         INTEGER," +
                    "\"user_id\"            INTEGER," +
                    "\"purchase_date\"      DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"cashier_id\") REFERENCES \"worker\"(\"user_id\")," +
                    "FOREIGN KEY(\"user_id\") REFERENCES \"user\"(\"id\")" +
                    ");";
            statement.addBatch(purchaseTable);

            String purchaseToProductTable = "CREATE TABLE IF NOT EXISTS \"item\" (" +
                    "\"purchase_id\"        INTEGER     NOT NULL," +
                    "\"product_id\"         INTEGER     NOT NULL," +
                    "\"amount\"             DOUBLE      NOT NULL," +
                    "\"individual_price\"   DOUBLE      NOT NULL," +
                    "PRIMARY KEY(\"purchase_id\", \"product_id\")," +
                    "FOREIGN KEY(\"purchase_id\") REFERENCES \"purchase\"(\"id\")," +
                    "FOREIGN KEY(\"product_id\") REFERENCES \"product\"(\"id\")" +
                    ");";
            statement.addBatch(purchaseToProductTable);

            statement.executeBatch();
        } catch (SQLException exception) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to create tables", exception);
        }
    }

    public static void addSampleData() {
        try {
            Statement statement = connection.createStatement();

            String createUsers = "INSERT INTO \"user\" (name, identification, email, phone_number) " +
                    "VALUES " +
                    "   ('Samuel Nascimento', '000.000.000-00', 'me@samnsc.com', NULL)," +
                    "   ('Gabriel Malosto', '000.000.000-01', 'me@gabdumal.com', '+55 (32) 90000-0000')," +
                    "   ('Gabriel Souza', '000.000.000-02', NULL, '+55 (31) 90000-0000') " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createUsers);

            String createClients = "INSERT INTO \"client\" (user_id) " +
                    "VALUES " +
                    "   (1)," +
                    "   (3) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createClients);

            String createWorkers = "INSERT INTO \"worker\" (user_id, username, password, worker_type) " +
                    "VALUES " +
                    "   (1, 'samnsc', 'b7e94be513e96e8c45cd23d162275e5a12ebde9100a425c4ebcdd7fa4dcd897c', 'MANAGER')," + // sha256sum hash for "senha"
                    "   (2, 'gabdumal', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'CASHIER')" + // sha256sum hash for "123456"
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createWorkers);

            String createProducts = "INSERT INTO \"product\" (name, product_code, measurement_type, selling_price) " +
                    "VALUES " +
                    "   ('Nescau', '0010001000015', 'UNIT', 10)," +
                    "   ('Batata', '0019999900017','KILOGRAM', 3.8)," + // stock em quilograma e preços por kilo
                    "   ('Alho', '0019999900024', 'KILOGRAM', 2.5)," +
                    "   ('Coca-Cola', '0010002000014', 'UNIT', 10)," +
                    "   ('Bife de Boi Fresco', '0019999900031','KILOGRAM', 30)," +
                    "   ('Bife de Boi Congelado - 1Kg', '0019999900048', 'UNIT', 25)," +
                    "   ('Biscoito', '0020003000012','UNIT', 7) " + // pedaços de bife de boi pré-cortados e separados em pacotes de 1kg cada
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createProducts);

            String createPurchases = "INSERT INTO \"purchase\" (purchase_price, payment_method, cashier_id, user_id, purchase_date) " +
                    "VALUES " +
                    "   (50, 'PIX', 2, 3, CURRENT_DATE)," +
                    "   (19, 'CREDIT_CARD', 2, 1, CURRENT_DATE)," +
                    "   (4, 'DEBIT_CARD', 1, 3, CURRENT_DATE) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createPurchases);

            String createPurchasesToProducts = "INSERT INTO \"item\" (purchase_id, product_id, amount, individual_price) " +
                    "VALUES " +
                    "   (1, 6, 2, 25)," +
                    "   (2, 1, 1, 10)," +
                    "   (2, 4, 1, 10)," +
                    "   (3, 4, 1, 4) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createPurchasesToProducts);

            statement.executeBatch();
        } catch (SQLException exception) {
            Logger.getLogger(Database.class.getName()).log(Level.WARNING, "Unable to add sample data", exception);
        }
    }
}
