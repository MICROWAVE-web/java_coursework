package parser;

import database.DatabaseManager;
import model.Order;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

public class OrderParser {
    private static final String PARSING_URL = "https://example.com/orders";

    public void parseAndNotify() {
        try {
            Document doc = Jsoup.connect(PARSING_URL).get();
            Elements orders = doc.select(".order-item"); // Замените на селектор сайта

            for (Element orderElement : orders) {
                String taskId = orderElement.attr("data-task-id");
                String title = orderElement.select(".order-title").text();
                String payment = orderElement.select(".order-payment").text();
                String description = orderElement.select(".order-description").text();
                String directUrl = orderElement.select(".order-link").attr("href");

                Order order = new Order(taskId, title, payment, description, directUrl);
                if (DatabaseManager.saveOrder(order)) {
                    notifyUsers(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyUsers(Order order) {
        Map<String, List<Long>> tagToUsers = DatabaseManager.getTagToUsersMapping();
        tagToUsers.forEach((tag, users) -> {
            if (order.getDescription().contains(tag)) {
                users.forEach(userId -> {
                    // Отправка уведомлений
                    // Реализуйте вызов Telegram API
                });
            }
        });
    }
}
