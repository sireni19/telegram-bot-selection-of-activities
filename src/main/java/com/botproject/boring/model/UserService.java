package com.botproject.boring.model;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    public void registerUser(Message message){

        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User(chatId,chat.getUserName(),new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("Save user by method registerUser() in UserService");
        }
    }
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

}
