package model;

import java.util.Collection;
import java.util.Collections;

public class Order {
    private String taskId;
    private String title;
    private String payment;
    private String description;
    private String directUrl;

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

    // Сеттеры
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
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
