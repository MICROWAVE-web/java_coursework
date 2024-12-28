package bot;

import database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;


public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    // Загружаем .env
    Dotenv dotenv = Dotenv.load();
    private final TelegramClient telegramClient = new OkHttpTelegramClient(dotenv.get("BOT_TOKEN"));


    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long userId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getUserName();

            //System.out.println(update.getMessage().getText());

            if (messageText.startsWith("/start")) {
                DatabaseManager.addUser(userId, username);

                try {
                    telegramClient.execute(new SendMessage(String.valueOf(userId), "Добро пожаловать! Используйте /addtag и /removetag для управления тегами."));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (messageText.startsWith("/addtag")) {
                String tag = messageText.replace("/addtag", "").trim();
                if (tag.length() != 0) {
                    DatabaseManager.addTagToUser(userId, tag);
                    SendMessage sendMessage = new SendMessage(String.valueOf(userId), "Тег \"" + tag + "\" добавлен.");

                    try {
                        telegramClient.execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } else {
                    SendMessage sendMessage = new SendMessage(String.valueOf(userId), "Пустой тег. Попробуйте ещё раз");

                    try {
                        telegramClient.execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }

            } else if (messageText.startsWith("/removetag")) {
                String tag = messageText.replace("/removetag", "").trim();
                DatabaseManager.removeTagFromUser(userId, tag);
                SendMessage sendMessage = new SendMessage(String.valueOf(userId), "Тег \"" + tag + "\" удалён.");
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (messageText.startsWith("/tags")) {
                String tags = DatabaseManager.getUserTags(userId);
                SendMessage sendMessage = new SendMessage(String.valueOf(userId), "Ваши теги: " + tags);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
