package com.frevi.notification.dto;

import java.util.List;

public record UserDto(String email,
                      String phoneNumber,
                      List<String> userFcmTokens) {
}
