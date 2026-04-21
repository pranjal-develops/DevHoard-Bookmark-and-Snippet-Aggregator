package com.devhoard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    private String url;
    private Set<String> categories;
    private String guestId;
}


