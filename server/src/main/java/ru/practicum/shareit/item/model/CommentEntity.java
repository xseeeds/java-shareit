package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.user.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "comments")
@Generated
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String text;

    @ManyToOne(targetEntity = ItemEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @ToString.Exclude
    ItemEntity item;

    @Column(name = "item_id")
    Long itemId;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @ToString.Exclude
    UserEntity author;

    @Column(name = "author_id")
    Long authorId;

    @Column(name = "created_date")
    LocalDateTime created;

}