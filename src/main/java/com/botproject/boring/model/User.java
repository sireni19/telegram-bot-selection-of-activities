package com.botproject.boring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class User {
    @Id
    private Long chatId;//этот Id присваивает телеграмм каждому пользователю, когда тот присоединяется к боту
    private String username;
    private Timestamp registeredAt;


}
