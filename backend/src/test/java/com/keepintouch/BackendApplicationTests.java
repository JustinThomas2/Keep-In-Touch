package com.keepintouch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.local-user-email=local@keep-in-touch.test")
class BackendApplicationTests extends AbstractPostgresIntegrationTest {

  @Test
  void contextLoads() {}
}
