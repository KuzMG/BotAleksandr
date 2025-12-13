package org.example;


import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            BotSession session = botsApplication.registerBot(args[0], new Bot());

            System.out.println(session.isRunning());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
