package com.devhoard.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.ZonedDateTime;

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
    private String category;
    @JsonProperty("isFavorite")
    private boolean isFavorite = false;
    @CreationTimestamp
    private ZonedDateTime createdAt;

    public Bookmark(String originalUrl, String title, String description, String imgUrl){
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.category = null;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

    public Bookmark(String originalUrl, String title, String description, String imgUrl, String category){
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.category = category;
        this.createdAt = ZonedDateTime.now();
        this.isFavorite = false;
    }

}
