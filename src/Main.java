import bot.MyTelegramBot;
import database.DatabaseManager;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parser.OrderParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Загружаем .env
        Dotenv dotenv = Dotenv.load();

        // Инициализация базы данных
        DatabaseManager.initDatabase();

        // Запуск Telegram-бота в отдельном потоке
        new Thread(() -> {
            try {
                String botToken = dotenv.get("BOT_TOKEN");
                TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
                botsApplication.registerBot(botToken, new MyTelegramBot());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }).start();

        // Периодический запуск парсинга и отправки уведомлений
        new Thread(() -> {
            OrderParser parser = new OrderParser();
            while (true) {
                try {
                    parser.parseAndNotify();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(Integer.parseInt(dotenv.get("PARSER_INTERVAL"))); // Ожидание 1 минуту
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
