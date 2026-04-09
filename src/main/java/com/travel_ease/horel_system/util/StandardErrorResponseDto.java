package com.travel_ease.horel_system.util;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardErrorResponseDto {
    private int code;
    private String message;
}
