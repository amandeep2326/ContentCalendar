package com.example.content_calendar.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "author")
public class Author  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private BigInteger followersCount;

    @ElementCollection
    @CollectionTable(name = "author_social_links", joinColumns = @JoinColumn(name = "author_id"))
    @Column(name = "link")
    private List<String> socialMediaLinks;

    // The user account linked to this author (if any)
    @OneToOne(mappedBy = "author")
    @JsonIgnore
    private User user;

    // All users who have subscribed to this author
    @ManyToMany(mappedBy = "subscribedAuthors")
    @JsonIgnore
    private Set<User> subscribers;



    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public BigInteger getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(BigInteger followersCount) {
        this.followersCount = followersCount;
    }

    public List<String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(List<String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }
}
