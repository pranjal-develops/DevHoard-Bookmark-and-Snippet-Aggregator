package com.devhoard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String title;

    @Column(length = 512)
    private String description;

    private String imgUrl;

    @ElementCollection
    @CollectionTable(name = "bookmark_categories")
    private Set<String> categories;

    @JsonProperty("isFavorite")
    private boolean isFavorite = false;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    private String guestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Bookmark(String url, String title, String description, String imgUrl, Set<String> categories) {
        this.originalUrl = url;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = categories;
    }
}

