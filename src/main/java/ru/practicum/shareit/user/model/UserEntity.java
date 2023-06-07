package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Value
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String email;
}