package org.example;

import kotlin.Pair;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    final SeleniumThread thread = new SeleniumThread();

    public Bot() throws IOException {
        thread.start();
    }

    private final TelegramClient telegramClient = new OkHttpTelegramClient(Config.botToken);

    private String messageWithUser(String text, String name) {
        String newName;
        if (name.equals("Михаил")) {
            newName = "Михаил Кузнецов";
        } else if (name.equals("MaxBekker")) {
            newName = "Максим Барзилович";
        } else if (name.equals("Илья")) {
            newName = "Илья Скатенок";
        } else if (name.equals("кирюша")) {
            newName = "Кирилл Ивакин";
        } else if (name.equals("Aleksandr")) {
            return "";
        } else if (name.equals("Андрей")) {
            newName = "Андрей Коршунов";
        } else {
            return text;
        }
        return text + ". Написал " + newName;

    }

    String[] nameBot = new String[]{"саня", "саша", "сашок", "сань", "саш", "александр", "сашка", "сасня", "санек", "@SashaKarandaw", "ссанина"};

    private boolean isWork(Update update) {
        if (!update.hasMessage())
            return false;
        if (update.getMessage().getText() == null)
            return false;
        String text = update.getMessage().getText().toLowerCase();
        boolean flag = false;
        for (String s : nameBot) {
            if (text.contains(s)) {
                flag = true;
                break;
            }
        }
        if (flag)
            return true;

        if (update.getMessage().isReply()) {
            String replyName = update.getMessage().getReplyToMessage().getFrom().getFirstName();

            return replyName.equals("Aleksandr Karandaw");
        } else {
            return false;
        }
    }

    @Override
    public void consume(Update update) {
        Message message = update.getMessage();
        if (message == null)
            return;
        String name = message.getFrom().getFirstName();
        System.out.println(name);
        System.out.println(message.getChatId().toString());

        if (isWork(update)) {
            Integer messageId = message.getMessageId();
            String text = messageWithUser(update.getMessage().getText(), name);
            if (text.isEmpty())
                return;
            synchronized (thread) {
                thread.queue.add(new Pair(text, messageId));
                thread.message = (id) -> {
                    SendVoice sendVoice = new SendVoice(
                            message.getChatId().toString(),
                            null,
                            new InputFile(new File("sanya.mp3")),
                            null,
                            id,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    telegramClient.execute(sendVoice);
                };
            }
        }
    }
}
