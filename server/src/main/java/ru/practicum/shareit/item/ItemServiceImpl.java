package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.StatusOfBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage itemRequestStorage;


    // Добавление вещи
    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Метод: create. itemDto - {}, ownerId - {}", itemDto, ownerId);

        User owner = UserMapper.toUser(userService.findUserById(ownerId));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestStorage.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не найден"));
        }

        ItemDto item = ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, owner, request)));
        log.info("Метод: create. Созданная вещь - {}", item);

        return item;
    }

    // Обновление вещи
    @Transactional
    @Override
    public ItemDto update(Long id, ItemDto newItem, Long ownerId) {
        log.info("Метод: update. id - {}, newItem - {}, owner - {}", id, newItem, ownerId);
        // validateOwner(owner);
        Item oldItem = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + id + " не найдена"));

        if (!(oldItem.getOwner().getId() == ownerId)) {
            throw new NotFoundException("Нельзя обновлять чужую вещь");
        }

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.save(oldItem));
    }

    // Получение вещи
    @Override
    public ItemDto findItemById(long id, long owner) {
        log.info("Метод: findItemById. {}, owner {}", id, owner);

        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД - " + id + " не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentStorage.findByItemId(id);

        if (itemDto.getOwner() == owner) {
            LocalDateTime now = LocalDateTime.now();

            Booking last = bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                    id, now, StatusOfBooking.APPROVED);
            Booking next = bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                    id, now, StatusOfBooking.APPROVED);

            itemDto.setLastBooking(last != null ? BookingMapper.toBookingDtoResponse(last, item, last.getBooker()) : null);
            itemDto.setNextBooking(next != null ? BookingMapper.toBookingDtoResponse(next, item, next.getBooker()) : null);
        }

        itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        return itemDto;
    }

    @Override
    public ItemDto findItemById(long id) {
        log.info("Метод: findItemById_2. {}", id);

        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ИД - " + id + " не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentStorage.findByItemId(id);


        LocalDateTime now = LocalDateTime.now();

        Booking last = bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                id, now, StatusOfBooking.APPROVED);
        Booking next = bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                id, now, StatusOfBooking.APPROVED);

        itemDto.setLastBooking(last != null ? BookingMapper.toBookingDtoResponse(last, item, last.getBooker()) : null);
        itemDto.setNextBooking(next != null ? BookingMapper.toBookingDtoResponse(next, item, next.getBooker()) : null);

        itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        return itemDto;
    }

    // Получение всех вещей пользователя
    @Override
    public List<ItemDto> getAllItemsFromUser(Long owner) {
        log.info("Метод: getAllItemsFromUser. {}", owner);

        return itemStorage.findAllByOwnerId(owner).stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);

                    List<Comment> comments = commentStorage.findByItemId(item.getId());
                    itemDto.setComments(CommentMapper.toCommentDtoList(comments));

                    LocalDateTime now = LocalDateTime.now();
                    Booking last = bookingStorage.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                            item.getId(), now, StatusOfBooking.APPROVED);
                    Booking next = bookingStorage.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), now, StatusOfBooking.APPROVED);

                    itemDto.setLastBooking(last != null ? BookingMapper.toBookingDtoResponse(last, item, last.getBooker()) : null);
                    itemDto.setNextBooking(next != null ? BookingMapper.toBookingDtoResponse(next, item, next.getBooker()) : null);

                    return itemDto;
                })
                .toList();
    }

    // Поиск вещи
    @Override
    public List<ItemDto> itemSearch(String text) {
        log.info("Метод: itemSearch. {}", text);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.itemSearch(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    // Добавление комментария
    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, String comment) {
        log.info("Метод: addComment. itemId {}, userId {}, comment {}", itemId, userId, comment);

        User user = UserMapper.toUser(userService.findUserById(userId));
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь с ID " + itemId + " не найдена"));

        boolean hasUserBookedItem = bookingStorage.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), StatusOfBooking.APPROVED);
        if (!hasUserBookedItem) {
            throw new RuntimeException("Только пользователи, арендовавшие вещь, могут оставлять комментарии");
        }

        Comment newComment = new Comment();
        newComment.setText(comment);
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentStorage.save(newComment));
    }

    // Получение вещей по запросу
    public List<ItemDto> getItemRequestById(Long userId, Long requestId) {

        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        List<Item> items = itemStorage.findByRequestId(requestId);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}