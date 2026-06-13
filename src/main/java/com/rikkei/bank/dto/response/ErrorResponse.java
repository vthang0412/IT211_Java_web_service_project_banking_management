package com.rikkei.bank.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;

    private int status;

    private String error;

    private String message;

    private List<String> errors;

    private String path;

}
