package com.devhoard.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @CreationTimestamp
    private ZonedDateTime createdAt;

    public Bookmark(String originalUrl, String title, String description, String imgUrl){
        this.originalUrl = originalUrl;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.createdAt = ZonedDateTime.now();
    }

}
