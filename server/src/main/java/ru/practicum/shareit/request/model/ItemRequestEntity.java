package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.user.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Generated
public class ItemRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String description;

    LocalDateTime created;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @ToString.Exclude
    UserEntity requester;

    @Column(name = "requester_id")
    Long requesterId;

}
