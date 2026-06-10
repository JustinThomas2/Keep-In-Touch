package com.keepintouch;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractPostgresIntegrationTest {

  @Container @ServiceConnection
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:18")
          .withDatabaseName("keep_in_touch_test")
          .withUsername("keep_in_touch")
          .withPassword("keep_in_touch");
}
