package org.example;

import kotlin.Pair;
import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class SeleniumThread extends Thread {
    interface Message {
        void send(Integer id) throws TelegramApiException;
    }
    Message message;
    public BlockingQueue<Pair<String, Integer>> queue = new LinkedBlockingQueue<>();
    private WebDriver driver;

    @Override
    public void run() {
        super.run();
        startSelenium();
        while (true) {
            if (!queue.isEmpty()) {
                System.out.println(queue);
                Pair<String, Integer> pair = queue.poll();
                assert pair != null;
                getVoice(pair.component1());
                try {
                    message.send(pair.component2());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getVoice(String text) {
        try {
            Thread.sleep(1500);
            driver.findElement(By.xpath("//*[@id=\"chat-body\"]/div[2]/div/div/div/div[1]/textarea")).sendKeys(text);
            driver.findElement(By.xpath("//*[@id=\"chat-body\"]/div[2]/div/div/div/div[2]/button")).click();
            Thread.sleep(15000);
            WebElement window = driver.findElement(By.xpath("//*[@id=\"chat-messages\"]/div[1]/div[1]/div/div/div[1]/div/div[1]/div[1]"));
            window = window.findElement(By.cssSelector("button[class*=size-4]"));
            Actions action = new Actions(driver);
            action.moveToElement(window, 0, 0);
            window.click();

            Thread.sleep(10000);
            for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
                if (entry.toString().contains("https://storage")) {

                    int i = entry.getMessage().indexOf("https://storage");

                    String str = entry.getMessage().substring(i, entry.getMessage().length());
                    i = str.indexOf('"');
                    str = str.substring(0, i);
                    URL url = new URL(str);
                    InputStream inputStream = url.openStream();
                    Files.copy(inputStream, new File("sanya.mp3").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSelenium() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions capabilities = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        capabilities.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);

        driver = new ChromeDriver(capabilities);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("cookie.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String jsonString;
        try {
            jsonString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        JSONArray jsonArray = new JSONArray(jsonString);
        driver.get("https://character.ai");

        int i = 9;
        String name = jsonArray.getJSONObject(i).getString("name");
        String value = jsonArray.getJSONObject(i).getString("value");
        String path = jsonArray.getJSONObject(i).getString("path");
        String domain = jsonArray.getJSONObject(i).getString("domain");
        Date expiry = new Date(jsonArray.getJSONObject(i).getLong("expiry"));
        String sameSite = jsonArray.getJSONObject(i).getString("sameSite");
        boolean secure = jsonArray.getJSONObject(i).getBoolean("secure");
        boolean httpOnly = jsonArray.getJSONObject(i).getBoolean("httpOnly");
        Cookie cookie = new Cookie(name, value, domain, path, expiry, secure, httpOnly, sameSite);
        driver.manage().addCookie(cookie);
        driver.get("https://character.ai/chat/riqZmhutPNFTRT0lx2bTkBJEOc3mwPDO5Wn6EXiCQ30");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        driver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/div/div[2]/div/button[2]")).click();
    }
}
