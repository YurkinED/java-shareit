package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    Boolean available;
    @Column(name = "user_id")
    long user;
}
