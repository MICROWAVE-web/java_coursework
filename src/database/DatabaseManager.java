package database;

import io.github.cdimascio.dotenv.Dotenv;
import model.Order;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    // Загружаем .env
    static Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DATABASE_URL");
    static int consoleLog = Integer.parseInt(dotenv.get("CONSOLE_LOG"));

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
                if ((consoleLog == 1)) {
                    System.out.println("Заказ с task_id = " + order.getTaskId() + " уже существует в базе данных. Пропускаем запись.");
                }
            } else {
                e.printStackTrace(); // Лог остальных ошибок
            }
            return false;
        }
    }

    public static Map<String, List<Long>> getTagToUsersMapping() {
        Map<String, List<Long>> tagToUsers = new HashMap<>();

        String query = "SELECT user_id, tags FROM users";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long userId = resultSet.getLong("user_id");
                String tagsString = resultSet.getString("tags"); // Теги в виде строки, разделенной запятыми
                System.out.println(tagsString);
                if (tagsString != null && !tagsString.isEmpty()) {
                    // Разбиваем строку тегов на массив
                    String[] tags = tagsString.split(",");

                    for (String tag : tags) {
                        tag = tag.trim(); // Убираем лишние пробелы
                        if (tag.length() > 0) {
                            // Если тега еще нет в словаре, добавляем его
                            tagToUsers.putIfAbsent(tag, new ArrayList<>());

                            // Добавляем ID пользователя к соответствующему тегу
                            tagToUsers.get(tag).add(userId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagToUsers;
    }

    public static void addTagToUser(long userId, String tag) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Получаем текущие теги пользователя
            PreparedStatement selectStmt = conn.prepareStatement("SELECT tags FROM users WHERE user_id = ?");
            selectStmt.setLong(1, userId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String existingTags = rs.getString("tags");
                Set<String> tagSet = new HashSet<>(Arrays.asList(existingTags.split(",")));

                // Добавляем новый тег
                if (!tagSet.contains(tag)) {
                    tagSet.add(tag);
                    String updatedTags = String.join(",", tagSet);

                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET tags = ? WHERE user_id = ?");
                    updateStmt.setString(1, updatedTags);
                    updateStmt.setLong(2, userId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeTagFromUser(long userId, String tag) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Получаем текущие теги пользователя
            PreparedStatement selectStmt = conn.prepareStatement("SELECT tags FROM users WHERE user_id = ?");
            selectStmt.setLong(1, userId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String existingTags = rs.getString("tags");
                Set<String> tagSet = new HashSet<>(Arrays.asList(existingTags.split(",")));

                // Удаляем тег
                if (tagSet.contains(tag)) {
                    tagSet.remove(tag);
                    String updatedTags = String.join(",", tagSet);

                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET tags = ? WHERE user_id = ?");
                    updateStmt.setString(1, updatedTags);
                    updateStmt.setLong(2, userId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
