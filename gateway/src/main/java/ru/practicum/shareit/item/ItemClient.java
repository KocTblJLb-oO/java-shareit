package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Long ownerId) {
        return post("", itemDto, ownerId);
    }

    public ResponseEntity<Object> update(Long itemId,ItemDto itemDto, Long ownerId) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAllItems(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, String text) {
        Map<String, String> comment = Collections.singletonMap("text", text);
        return post("/" + itemId + "/comment", comment, userId);
    }
}