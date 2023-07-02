package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Value
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(unique = true)
    String email;
}