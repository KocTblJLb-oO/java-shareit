package ru.practicum.shareit.IntegrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserStorage userStorage;

    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        // Создаём владельца
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@ya.ru");
        owner = userStorage.save(owner);

        // Создаём бронирующего
        booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@ya.ru");
        booker = userStorage.save(booker);
    }

    @Test
    void getAllItemsFromUser() {
        Item item = new Item();
        item.setName("Название");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);
        ItemDto itemDto = itemService.create(ItemMapper.toItemDto(item), owner.getId());
        Long itemId = itemDto.getId();

        BookingDto pastBooking = new BookingDto();
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setItemId(itemId);
        BookingDtoResponse booking = bookingService.create(pastBooking, booker.getId());

        bookingService.approveBooking(booking.getId(), true, owner.getId());

        List<ItemDto> result = itemService.getAllItemsFromUser(owner.getId());

        assertThat(result).hasSize(1);
        ItemDto resultItem = result.get(0);
        assertThat(resultItem.getName()).isEqualTo("Название");
    }
}