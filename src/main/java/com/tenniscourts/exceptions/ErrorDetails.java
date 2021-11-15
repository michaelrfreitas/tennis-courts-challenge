package com.tenniscourts.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ErrorDetails {
    public ErrorDetails(LocalDateTime now, String message2, String description) {
    }
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
