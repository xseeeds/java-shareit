package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.user.model.UserEntity;

import javax.persistence.*;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, unique = true, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @ToString.Exclude
    private UserEntity owner;

    @Column(name = "owner_id")
    private Long ownerId;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

}
