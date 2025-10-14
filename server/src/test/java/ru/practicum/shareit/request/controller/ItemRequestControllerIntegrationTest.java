package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @Test
    void itemRequestLifecycle_IntegrationTest() throws Exception {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@test.com"));
        User owner = userRepository.save(new User(null, "Owner", "owner@test.com"));

        CreateItemRequestDTO requestDto = new CreateItemRequestDTO();
        requestDto.setDescription("Need a drill");

        String requestJson = objectMapper.writeValueAsString(requestDto);

        String responseJson = mockMvc.perform(post("/requests")
                        .header(HEADER_REQUEST_ID, requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        ItemRequestDto createdRequest = objectMapper.readValue(responseJson, ItemRequestDto.class);
        long requestId = createdRequest.getId();

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(HEADER_REQUEST_ID, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.items").isEmpty());

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("A powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        String itemJson = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
                        .header(HEADER_REQUEST_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(HEADER_REQUEST_ID, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].name").value("Drill"));
    }
}
