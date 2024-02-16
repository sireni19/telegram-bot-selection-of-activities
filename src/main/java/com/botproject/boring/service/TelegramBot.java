package com.botproject.boring.service;

import com.botproject.boring.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand("/start", "Bot starts working and get welcome message"));
        menu.add(new BotCommand("/show", "Show a random event"));
        try {
            execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null) {
            });
        } catch (TelegramApiException e) {
           log.error("Error setting bot`s command menu: "+e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageTest) {
                case "/start":
                    log.info("Method start");
                    startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                    break;

                default:
                    sendMessage(chatId, "Incorrect command");
            }
        }
    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name;
        sendMessage(chatId, answer);
        log.info("Replied to user, method startCommandReceived() from TelegramBot");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();//класс для отправки сообщений
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
