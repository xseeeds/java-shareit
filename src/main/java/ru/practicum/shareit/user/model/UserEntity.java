package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;
}