package com.devhoard.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String title;
    @Column(name = "description", nullable = true,length = 512)
    private String description;
    private String imgUrl;
    @ElementCollection
    @CollectionTable //This tells JPA to create a separate table behind the scenes to keep track of all the tags without us needing to do it manually!
    private Set<String> categories;
    @JsonProperty("isFavorite")
    private boolean isFavorite = false;
    @CreationTimestamp
    private ZonedDateTime createdAt;

    public Bookmark(String originalUrl, String title, String description, String imgUrl){
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = null;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    public Bookmark(String originalUrl, String title, String description, String imgUrl, Set<String> categories){
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.categories = categories;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

}
