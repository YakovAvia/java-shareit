package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.RequestBookingCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static final String HEADER_REQUEST_ID = "X-Sharer-User-Id";

    @Test
    void bookingLifecycle_IntegrationTest() throws Exception {

        User owner = userRepository.save(new User(null, "Owner", "owner@test.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@test.com"));

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setUser(owner);
        Item savedItem = itemRepository.save(item);

        RequestBookingCreateDto bookingDto = new RequestBookingCreateDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        String responseJson = mockMvc.perform(post("/bookings")
                        .header(HEADER_REQUEST_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andReturn().getResponse().getContentAsString();

        BookingCreateDto createdBooking = objectMapper.readValue(responseJson, BookingCreateDto.class);
        long bookingId = createdBooking.getId();

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HEADER_REQUEST_ID, owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HEADER_REQUEST_ID, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
