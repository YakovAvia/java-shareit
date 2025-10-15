package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @Test
    void getItem_whenItemExists_shouldReturnItem() throws Exception {

        long itemId = 1L;
        long userId = 1L;

        GetItemDto getItemDto = new GetItemDto();
        getItemDto.setId(itemId);
        getItemDto.setName("Test Item");
        getItemDto.setDescription("Test Description");
        getItemDto.setAvailable(true);
        getItemDto.setComments(new ArrayList<>());

        when(itemService.getItem(itemId, userId)).thenReturn(getItemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(HEADER_REQUEST_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }
}
