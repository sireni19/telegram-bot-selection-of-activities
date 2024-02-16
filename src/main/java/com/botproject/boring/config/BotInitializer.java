package com.botproject.boring.config;

import com.botproject.boring.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * BotInitializer является компонентом Spring, который выполняет инициализацию и регистрацию бота
 * при запуске приложения.
 */
@Component
@Slf4j
public class BotInitializer {
    @Autowired
    private TelegramBot bot;
    @EventListener({ContextRefreshedEvent.class})
    /**
     * При создании и инициализации контекста Spring, автоматически отработает метод init(),
    так как он помечен аннотацией @EventListener({ContextRefreshedEvent.class}).
    ContextRefreshedEvent - это событие, которое возникает, когда контекст приложения полностью
    инициализирован и готов к работе.

    DefaultBotSession - это класс из TelegramBots, который представляет сеанс подключения к Telegram API.
    Он предоставляет функциональность для установления связи с Telegram и обработки обновлений.
     */
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        }catch (TelegramApiException e){
            log.error("Error in method init() from BotInitializer class: "+e.getMessage());
        }
    }
}
