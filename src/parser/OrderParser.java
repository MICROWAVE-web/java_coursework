package parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import model.Order;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class OrderParser {


    // Загружаем .env
    Dotenv dotenv = Dotenv.load();

    // Телеграмм клиент
    private final TelegramClient telegramClient = new OkHttpTelegramClient(dotenv.get("BOT_TOKEN"));

    // Читаем переменные из .env
    int consoleLog = Integer.parseInt(dotenv.get("CONSOLE_LOG"));
    private static final String PARSING_URL = "https://freelance.habr.com/tasks";
    private static final String cookiesFilePath = "cookies/cookies.txt"; // Путь к файлу с cookies

    public void parseAndNotify() throws IOException {

        // Чтение cookies из файла
        Map<String, String> cookies = readCookiesFromFile();

        if (consoleLog == 1) {
            System.out.println("cookies: " + cookies);
        }

        try {
            Document doc = Jsoup.connect(PARSING_URL)
                    .cookies(cookies)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36") // Установка User-Agent
                    .timeout(10000).get();
            Elements orders = doc.select(".task-card__wrapper"); // Замените на селектор сайта

            for (Element orderElement : orders) {
                String taskId = orderElement.select(".task-card__link").attr("href").replace("/tasks/", "");
                String title = orderElement.select(".task-card__heading").text();
                String payment = orderElement.select(".task-card__price").text();
                String description = orderElement.select(".task-card__description").text();
                String directUrl = PARSING_URL + orderElement.select(".task-card__link").attr("href").replace("tasks/", "");

                Order order = new Order(taskId, title, payment, description, directUrl);

                if (consoleLog == 1) {
                    order.printOrder();
                }

                if (DatabaseManager.saveOrder(order)) {
                    notifyUsers(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Читает cookies из файла и возвращает их в виде Map.
     *
     * @return Map с парами "ключ-значение" cookies.
     * @throws IOException Если файл не найден или возникла ошибка чтения.
     */
    private static Map<String, String> readCookiesFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(OrderParser.cookiesFilePath))) {
            // Чтение содержимого файла (ожидается JSON-строка)
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            // Парсинг JSON в Map
            String json = jsonBuilder.toString();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
    }

    private void notifyUsers(Order order) {
        Map<String, List<Long>> tagToUsers = DatabaseManager.getTagToUsersMapping();
        tagToUsers.forEach((tag, users) -> {
            if (consoleLog == 1) {
                System.out.println("tag: " + tag + " users: " + users);
            }
            if (order.getDescription().contains(tag)) {
                users.forEach(userId -> {
                    SendMessage sendMessage = new SendMessage(String.valueOf(userId),
                            "Новый заказ: " + order.getTitle() + "\n\n" +
                                    "Описание: " + order.getDescription() + "\n\n" +
                                    "Платеж: " + order.getPayment().replace("₽", "₽ ") + "\n\n" +
                                    "Подробнее: " + order.getDirectUrl());


                    try {
                        telegramClient.execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
