package com.nestuity.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test - requires database connection.
 * Run separately from unit tests.
 */
@SpringBootTest
@Disabled("Integration test - requires database. Run separately with: ./gradlew integrationTest")
class NestuityServiceApplicationTests {

    @Test
    void contextLoadsTest() {
    }

}
