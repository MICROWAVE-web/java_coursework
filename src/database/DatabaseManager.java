package database;

import io.github.cdimascio.dotenv.Dotenv;
import model.Order;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    // Загружаем .env
    static Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DATABASE_URL");

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY, username TEXT, tags TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (task_id TEXT PRIMARY KEY, title TEXT, payment TEXT, description TEXT, direct_url TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(long userId, String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO users (user_id, username, tags) VALUES (?, ?, ?)");
            pstmt.setLong(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, "");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveOrder(Order order) {
        String insertOrderQuery = "INSERT INTO orders (task_id, title, payment, description, direct_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertOrderQuery)) {

            // Устанавливаем параметры
            preparedStatement.setString(1, order.getTaskId());
            preparedStatement.setString(2, order.getTitle());
            preparedStatement.setString(3, order.getPayment());
            preparedStatement.setString(4, order.getDescription());
            preparedStatement.setString(5, order.getDirectUrl());

            // Выполняем запрос
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Обработка исключения при нарушении PRIMARY KEY
            if (e.getMessage().contains("PRIMARY KEY constraint failed")) {
                System.out.println("Заказ с task_id = " + order.getTaskId() + " уже существует в базе данных. Пропускаем запись.");
            } else {
                e.printStackTrace(); // Лог остальных ошибок
            }
            return false;
        }
    }


    public static Map<String, List<Long>> getTagToUsersMapping() {
        // Реализация получения тегов и пользователей
        return null;
    }

    public static void addTagToUser(long userId, String tag) {
    }

    public static void removeTagFromUser(long userId, String tag) {
    }
}
