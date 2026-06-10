package com.keepintouch.service;

import com.keepintouch.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CurrentUserService {

  private final UserService userService;

  private final String localUserEmail;

  public CurrentUserService(
      UserService userService, @Value("${app.local-user-email}") String localUserEmail) {
    this.userService = userService;
    this.localUserEmail = localUserEmail;
  }

  public User getCurrentUser() {
    return userService
        .findByEmail(localUserEmail)
        .orElseThrow(() -> new IllegalStateException("Seeded local user was not found."));
  }
}
