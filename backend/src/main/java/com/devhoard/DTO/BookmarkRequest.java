package com.devhoard.DTO;

import java.util.Set;

@jakarta.validation.constraints.NotNull
@lombok.Data
public class BookmarkRequest {

    public String url;
    public Set<String> categories;
    private String guestId;
}
