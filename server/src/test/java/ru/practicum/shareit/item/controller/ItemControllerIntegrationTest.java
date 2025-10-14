package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @Test
    void createAndGetItem_IntegrationTest() throws Exception {

        User user = new User();
        user.setName("Test User");
        user.setEmail("integration@test.com");
        User savedUser = userRepository.save(user);
        long userId = savedUser.getId();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Integration Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        String itemJson = objectMapper.writeValueAsString(itemDto);

        String responseJson = mockMvc.perform(post("/items")
                        .header(HEADER_REQUEST_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Item"))
                .andReturn().getResponse().getContentAsString();

        ItemDto createdItem = objectMapper.readValue(responseJson, ItemDto.class);
        long itemId = createdItem.getId();

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(HEADER_REQUEST_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Integration Test Item"));
    }
}
