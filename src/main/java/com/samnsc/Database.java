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
                    "\"worker_type\"    TEXT        NOT NULL CHECK(\"worker_type\" IN ('CASHIER', 'MANAGER', 'WAREHOUSE'))," +
                    "\"start_date\"     DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "\"end_date\"       DATE," +
                    "PRIMARY KEY(\"user_id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"user_id\") REFERENCES \"user\"(\"id\")," +
                    "CHECK (end_date IS NULL OR start_date < end_date)" +
                    ");";
            statement.addBatch(workerTable);

            String couponTable = "CREATE TABLE IF NOT EXISTS \"coupon\" (" +
                    "\"id\"                     INTEGER     NOT NULL UNIQUE," +
                    "\"code\"                   TEXT        NOT NULL UNIQUE," +
                    "\"valid_until\"            DATE," +
                    "\"discount_amount\"        DOUBLE," +
                    "\"discount_percentage\"    DOUBLE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)," +
                    "CHECK (discount_amount IS NOT NULL AND discount_percentage IS NULL OR discount_amount IS NULL AND discount_percentage IS NOT NULL)" +
                    ");";
            statement.addBatch(couponTable);

            String couponToClientTable = "CREATE TABLE IF NOT EXISTS \"coupon_to_client\" (" +
                    "\"coupon_id\"             INTEGER     NOT NULL," +
                    "\"eligible_client_id\"    INTEGER," +
                    "PRIMARY KEY(\"coupon_id\", \"eligible_client_id\")," +
                    "FOREIGN KEY(\"coupon_id\") REFERENCES \"coupon\"(\"id\")," +
                    "FOREIGN KEY(\"eligible_client_id\") REFERENCES \"client\"(\"user_id\")" +
                    ");";
            statement.addBatch(couponToClientTable);

            String productTable = "CREATE TABLE IF NOT EXISTS \"product\" (" +
                    "\"id\"                     INTEGER     NOT NULL UNIQUE," +
                    "\"name\"                   TEXT        NOT NULL," +
                    "\"product_code\"           TEXT        NOT NULL UNIQUE," + // codificação seguindo o padrão EAN-13 (3 primeiros números são o código do país, tamanho variável do código da marca, tamanho variável do código do produto e um check-digit)
                    "\"measurement_type\"       TEXT        NOT NULL CHECK(\"measurement_type\" IN ('UNIT', 'KILOGRAM'))," +
                    "\"stock\"                  DOUBLE      NOT NULL," +
                    "\"market_purchase_price\"  DOUBLE," +
                    "\"selling_price\"          DOUBLE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)" +
                    ");";
            statement.addBatch(productTable);

            String couponToProductTable = "CREATE TABLE IF NOT EXISTS \"coupon_to_product\" (" +
                    "\"coupon_id\"          INTEGER     NOT NULL," +
                    "\"valid_product_id\"   INTEGER     NOT NULL," +
                    "PRIMARY KEY(\"coupon_id\", \"valid_product_id\")," +
                    "FOREIGN KEY(\"coupon_id\") REFERENCES \"coupon\"(\"id\")," +
                    "FOREIGN KEY(\"valid_product_id\") REFERENCES \"product\"(\"id\")" +
                    ");";
            statement.addBatch(couponToProductTable);

            String discountTable = "CREATE TABLE IF NOT EXISTS \"discount\" (" +
                    "\"id\"                 INTEGER     NOT NULL UNIQUE," +
                    "\"product_id\"         INTEGER     NOT NULL," +
                    "\"original_price\"     DOUBLE      NOT NULL," +
                    "\"discounted_price\"   DOUBLE      NOT NULL," +
                    "\"valid_until\"        DATE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"product_id\") REFERENCES \"product\"(\"id\")" +
                    ");";
            statement.addBatch(discountTable);

            String restockTable = "CREATE TABLE IF NOT EXISTS \"restock\" (" +
                    "\"id\"                 INTEGER     NOT NULL UNIQUE," +
                    "\"restocked_item_id\"  INTEGER     NOT NULL," +
                    "\"amount_added\"       INTEGER     NOT NULL," +
                    "\"restock_date\"       DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "\"worker_id\"          INTEGER," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"restocked_item_id\") REFERENCES \"product\"(\"id\")," +
                    "FOREIGN KEY(\"worker_id\") REFERENCES \"worker\"(\"user_id\")" +
                    ");";
            statement.addBatch(restockTable);

            String purchaseTable = "CREATE TABLE IF NOT EXISTS \"purchase\" (" +
                    "\"id\"                 INTEGER     NOT NULL UNIQUE," +
                    "\"purchase_price\"     DOUBLE      NOT NULL," +
                    "\"payment_method\"     TEXT        CHECK(\"payment_method\" IN ('MONEY', 'CREDIT_CARD', 'DEBIT_CARD', 'CHECK', 'PIX')) NOT NULL," +
                    "\"cashier_id\"         INTEGER," +
                    "\"client_id\"          INTEGER," +
                    "\"purchase_date\"      DATE        NOT NULL DEFAULT CURRENT_DATE," +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)," +
                    "FOREIGN KEY(\"cashier_id\") REFERENCES \"worker\"(\"user_id\")," +
                    "FOREIGN KEY(\"client_id\") REFERENCES \"client\"(\"user_id\")" +
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

            String purchaseToCouponTable = "CREATE TABLE IF NOT EXISTS \"purchase_to_coupon\" (" +
                    "\"purchase_id\"    INTEGER     NOT NULL," +
                    "\"coupon_id\"      INTEGER     NOT NULL," +
                    "PRIMARY KEY(\"purchase_id\", \"coupon_id\")," +
                    "FOREIGN KEY(\"purchase_id\") REFERENCES \"purchase\"(\"id\")," +
                    "FOREIGN KEY(\"coupon_id\") REFERENCES \"coupon\"(\"id\")" +
                    ");";
            statement.addBatch(purchaseToCouponTable);

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
                    "   ('Gabriel Souza', '000.000.000-02', NULL, NULL)," +
                    "   ('Gleiph Ghioto', '000.000.000-03', NULL, '+55 (31) 90000-0000') " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createUsers);

            String createClients = "INSERT INTO \"client\" (user_id) " +
                    "VALUES " +
                    "   (1)," +
                    "   (4) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createClients);

            String createWorkers = "INSERT INTO \"worker\" (user_id, username, password, worker_type) " +
                    "VALUES " +
                    "   (1, 'samnsc', 'b7e94be513e96e8c45cd23d162275e5a12ebde9100a425c4ebcdd7fa4dcd897c', 'MANAGER')," + // sha256sum hash for "senha"
                    "   (2, 'gabdumal', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'CASHIER')," + // sha256sum hash for "123456"
                    "   (3, 'ghdesouza', '55a5e9e78207b4df8699d60886fa070079463547b095d1a05bc719bb4e6cd251', 'WAREHOUSE') " + // sha256sum hash for "senha123"
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createWorkers);

            String createCoupons = "INSERT INTO \"coupon\" (code, valid_until, discount_amount, discount_percentage) " +
                    "VALUES " +
                    "   ('CUPOM10%', CURRENT_DATE - 1, NULL, 10)," + // teste para um cupom que não é mais válido
                    "   ('CUPOM20%', CURRENT_DATE + 1, NULL, 20)," + // teste para um cupom que ainda é válido
                    "   ('DESCONTOFUNCIONARIOS', NULL, NULL, 5)," + // teste para um cupom que nunca expira
                    "   ('CUPOM5', CURRENT_DATE, 5, NULL)," + // teste para cupom com desconto fixo
                    "   ('CUPOM5COCA', NULL, 5, NULL) " + // teste para um cupom que só é válido para um produto
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createCoupons);

            // não ter uma relação nessa tabela significa que nenhum cliente pode usar esse cupom
            String createCouponsToClients = "INSERT INTO \"coupon_to_client\" (coupon_id, eligible_client_id) " +
                    "VALUES " +
                    "   (1, 1)," +
                    "   (2, 1)," +
                    "   (4, 4)," +
                    "   (5, 1) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createCouponsToClients);

            String createCouponsToClientsForWorkers = "INSERT INTO \"coupon_to_client\" (coupon_id, eligible_client_id) " +
                    "SELECT " +
                    "   3, \"worker\".user_id " +
                    "       FROM \"worker\" " +
                    "       INNER JOIN \"client\" " +
                    "           ON \"worker\".user_id == \"client\".user_id " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createCouponsToClientsForWorkers);

            String createProducts = "INSERT INTO \"product\" (name, product_code, measurement_type, stock, market_purchase_price, selling_price) " +
                    "VALUES " +
                    "   ('Nescau', '0010001000015', 'UNIT', 50, 5, 10)," +
                    "   ('Batata', '0019999900017','KILOGRAM', 120.85, 2.3, 3.8)," + // stock em quilograma e preços por kilo
                    "   ('Alho', '0019999900024', 'KILOGRAM', 230.32, 2, 2.5)," +
                    "   ('Coca-Cola', '0010002000014', 'UNIT', 30, 4, 10)," +
                    "   ('Bife de Boi Fresco', '0019999900031','KILOGRAM', 240, 20, 30)," +
                    "   ('Bife de Boi Congelado - 1Kg', '0019999900048', 'UNIT', 20, 15, 25)," +
                    "   ('Biscoito', '0020003000012','UNIT', 150, 4, 7) " + // pedaços de bife de boi pré-cortados e separados em pacotes de 1kg cada
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createProducts);

            // não ter uma relação nessa tabela significa que todos os produtos são válidos para esse cupom
            String createCouponsToProducts = "INSERT INTO \"coupon_to_product\" (coupon_id, valid_product_id) " +
                    "VALUES " +
                    "   (5, 4) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createCouponsToProducts);

            String createDiscounts = "INSERT INTO \"discount\" (product_id, original_price, discounted_price, valid_until) " +
                    "VALUES " +
                    "   (4, 10, 4, CURRENT_DATE + 1)," + // esse é um teste para garantir que o valor de um produto nunca será negativo (desconto com cupom de 5 reais)
                    "   (1, 10, 7, NULL)," + // válido para sempre
                    "   (2, 3.8, 3.5, CURRENT_DATE - 1)," + // teste para um desconto expirado
                    "   (7, 7, 6, CURRENT_DATE + 1)," +
                    "   (7, 7, 5, CURRENT_DATE + 1) " + // teste para garantir que dentre dois cupons válidos o com maior desconto sempre é aplicado
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createDiscounts);

            String createRestocks = "INSERT INTO \"restock\" (restocked_item_id, amount_added, restock_date, worker_id) " +
                    "VALUES " +
                    "   (1, 50, CURRENT_DATE, 3)," +
                    "   (2, 100, CURRENT_DATE, 3)," +
                    "   (2, 20.85, CURRENT_DATE, 1)," +
                    "   (3, 230.32, CURRENT_DATE, 1)," +
                    "   (4, 10, CURRENT_DATE, 1)," +
                    "   (4, 20, CURRENT_DATE, 3)," +
                    "   (5, 240, CURRENT_DATE, 3)," +
                    "   (6, 20, CURRENT_DATE, 3)," +
                    "   (7, 150, CURRENT_DATE, 3) " +
                    "ON CONFLICT DO NOTHING;";
            statement.addBatch(createRestocks);

            String createPurchases = "INSERT INTO \"purchase\" (purchase_price, payment_method, cashier_id, client_id, purchase_date) " +
                    "VALUES " +
                    "   (50, 'PIX', 2, 4, CURRENT_DATE)," +
                    "   (19, 'CREDIT_CARD', 2, 1, CURRENT_DATE)," +
                    "   (4, 'DEBIT_CARD', 1, 4, CURRENT_DATE) " +
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

            String createPurchasesToCoupons = "INSERT INTO \"purchase_to_coupon\" (purchase_id, coupon_id) " +
                    "VALUES " +
                    "   (2, 3) " +
                    "ON CONFLICT DO NOTHING";
            statement.addBatch(createPurchasesToCoupons);

            statement.executeBatch();
        } catch (SQLException exception) {
            Logger.getLogger(Database.class.getName()).log(Level.WARNING, "Unable to add sample data", exception);
        }
    }
}
