package model;

import java.util.Collection;
import java.util.Collections;

public class Order {
    private final String taskId;
    private final String title;
    private final String payment;
    private final String description;
    private final String directUrl;

    public Order(String taskId, String title, String payment, String description, String directUrl) {
        this.taskId = taskId;
        this.title = title;
        this.payment = payment;
        this.description = description;
        this.directUrl = directUrl;
    }

    // Геттеры
    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getPayment() {
        return payment;
    }

    public String getDescription() {
        return description;
    }

    public String getDirectUrl() {
        return directUrl;
    }

    // Метод вывода заказа в консоль
    public void printOrder() {
        System.out.println("Order Details:");
        System.out.println("Task ID: " + taskId);
        System.out.println("Title: " + title);
        System.out.println("Payment: " + payment);
        System.out.println("Description: " + description);
        System.out.println("Direct URL: " + directUrl);
        System.out.println();
    }
}
