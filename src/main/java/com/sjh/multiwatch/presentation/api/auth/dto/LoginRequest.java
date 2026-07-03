package com.sjh.multiwatch.presentation.api.auth.dto;

public record LoginRequest (
        String email,
        String password
){}
