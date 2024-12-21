import bot.MyTelegramBot;
import database.DatabaseManager;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parser.OrderParser;

public class Main {
    public static void main(String[] args) {
        // Инициализация базы данных
        DatabaseManager.initDatabase();

        // Запуск Telegram-бота в отдельном потоке
        new Thread(() -> {
            try {
                String botToken = "7749437855:AAHq_1omrDXXCmQCgMOb3kY_aD3qbBg1waU";
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
                parser.parseAndNotify();
                try {
                    Thread.sleep(60000); // Ожидание 1 минуту
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
