package com.example.content_calendar.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    // validation: not null, length constraints, etc. can be added as needed
    @Column(nullable = false, length = 255)
    String title;
    @Column(length = 1000)
    String description;
    @JoinColumn(name = "author_id")
    @ManyToOne
    Author author;  
    LocalDateTime publishedDate;
    LocalDateTime upDatedAt;

    @Enumerated(EnumType.STRING)
    Status status;

    @Enumerated(EnumType.STRING)
    Type type;

    @Column(nullable = false)
    boolean premium = false; // false = free, true = premium (subscription required)

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "content_tags",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    Set<Tags> tags; // A content can have multiple tags


    // getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Author getAuthor() {
        return author;
    }
    public void setAuthor(Author author) {
        this.author = author;
    }
    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
    public LocalDateTime getUpDatedAt() {
        return upDatedAt;
    }
    public void setUpDatedAt(LocalDateTime upDatedAt) {
        this.upDatedAt = upDatedAt;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Set<Tags> getTags() {
        return tags;
    }
    public void setTags(Set<Tags> tags) {
        this.tags = tags;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}