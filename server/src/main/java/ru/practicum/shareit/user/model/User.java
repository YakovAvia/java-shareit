package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

}
