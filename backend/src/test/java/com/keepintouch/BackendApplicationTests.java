package com.keepintouch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.config.import=optional:file:../.env[.properties]",
		"app.local-user-email=local@keep-in-touch.test"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
