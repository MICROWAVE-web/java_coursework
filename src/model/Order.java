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

    public Collection<Object> getDescription() {
        return Collections.singleton(this.description);
    }

    // Геттеры и сеттеры
}
