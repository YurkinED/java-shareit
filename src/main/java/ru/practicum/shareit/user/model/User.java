package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users", schema = "shareit")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String email;
}
