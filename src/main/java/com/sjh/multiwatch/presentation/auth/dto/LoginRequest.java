package com.sjh.multiwatch.presentation.auth.dto;

public record LoginRequest (
        String email,
        String password
){}
