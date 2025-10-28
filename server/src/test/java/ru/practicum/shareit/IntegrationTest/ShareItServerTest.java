package ru.practicum.shareit.IntegrationTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.ShareItServer;

class ShareItServerTest {

    @Test
    void main_shouldStartApplication() {
        ShareItServer.main(new String[]{});
    }
}