package database;

import model.Order;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:freelance_bot.db";

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
        // Реализация сохранения заказа
        return false;
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
