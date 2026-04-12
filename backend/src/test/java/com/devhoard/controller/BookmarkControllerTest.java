package com.devhoard.controller;

import com.devhoard.security.SecurityConfig;
import com.devhoard.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(SecurityConfig.class) // FORCE Spring to use your custom security
public class BookmarkControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private BookmarkService bookmarkService;

    @BeforeEach
    public void setup() {
        // This bridges your @WithMockUser mask into the Controller!
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "dragon")
    void deleteBookmark_ShouldHandCorrectIdentityToService() throws Exception {
        mockMvc.perform(delete("/api/bookmarks/1")
                        .param("guestId", "guest_777"))
                .andExpect(status().isNoContent());

        verify(bookmarkService).deleteBookmark(1L, "dragon", "guest_777");
    }
}
