package com.example.content_calendar.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // A user CAN be an author (nullable — null means regular user)
    @OneToOne
    @JoinColumn(name = "author_id", unique = true)
    private Author author;

    // A user can subscribe to multiple authors
    @ManyToMany
    @JoinTable(
        name = "user_subscriptions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> subscribedAuthors;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
