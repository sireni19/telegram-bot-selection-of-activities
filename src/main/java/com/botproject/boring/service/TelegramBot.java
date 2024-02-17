package com.botproject.boring.service;

import com.botproject.boring.config.BotConfig;
import com.botproject.boring.model.User;
import com.botproject.boring.model.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    public static final String EVENT = "EVENT";
    private final BotConfig config;
    @Autowired
    private UserService userService;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand("/start", "Bot starts working and get welcome message"));
        menu.add(new BotCommand("/show", "Show a random event"));
        try {
            execute(new SetMyCommands(menu, new BotCommandScopeDefault(), null) {
            });
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command menu: " + e.getMessage());
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

    /**
     * Представляет собой обновление чата, содержит набор функций, которые бот может
     * предоставить пользователю
     *
     * @param update Update received - входящее обновление
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && chatId==(config.getHostId())) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring((messageText.indexOf(" "))));
                var users = userService.findAllUsers();
                for (User user : users) {
                    sendMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (messageText) {
                    case "/start":
                        log.info("Method start");
                        userService.registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                        break;
                    case "/show":
                        show(update.getMessage().getChatId());
                        break;
                    case "/send":
                        break;
                    default:
                        sendMessage(chatId, "Incorrect command");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals(EVENT)) {
                show(chatId);
            }
        }
    }

    /**
     * Метод для отправки приветственного сообщения, когда новый пользователь регистрируется
     *
     * @param chatId идентификатор, по которому бот определяет какой перед ним пользователь
     * @param name   имя пользователя по его аккаунту
     */
    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");
        sendMessage(chatId, answer);
        log.info("Replied to user, method startCommandReceived() from TelegramBot");
    }

    /**
     * Метод, который предлагает пользователю подобрать случайное занятие,
     * содержит кнопку, прикрепленную к сообщению
     *
     * @param chatId идентификатор, по которому бот определяет какой перед ним пользователь
     */
    private void show(long chatId) {
        //TODO сюда внести будущий метод, который будет работать со сторонним API
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Here will be information about new random event");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//виртуальная клавиатура
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();//ряды кнопок
        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();//кнопки в ряду
        InlineKeyboardButton showButton = new InlineKeyboardButton();
        showButton.setText("Pick a new event ");
        showButton.setCallbackData(EVENT);//идентификатор кнопки, то есть то, как отличать кнопки друг от друга
        buttonsInRow.add(showButton);
        rows.add(buttonsInRow);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);//копирует клавиатуру к каждому сообщению бота
        executeMessage(message);
    }

    /**
     * Метод, которым бот отправляет пользователю сообщение о приветствии, рассылки или сообщение
     * об ошибке команды пользователя
     *
     * @param chatId идентификатор, по которому бот определяет какой перед ним пользователь
     * @param text   содержимое ответа, который дает бот пользователю в ответ на его запрос
     */
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
    }

    /**
     * Исполнение отправки ответного сообщения ботом пользователю
     *
     * @param message SendMessage - класс-сообщение, которое бот отправляет пользователю
     */
    private void executeMessage(SendMessage message) {
        try {
            log.info("Executor is working " + new Date());
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}

