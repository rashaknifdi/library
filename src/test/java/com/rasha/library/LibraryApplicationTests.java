package com.rasha.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.cloud.vault.enabled=false")
class LibraryApplicationTests {

    @Test
    void contextLoads() {
    }
}
