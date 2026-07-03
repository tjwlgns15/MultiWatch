package com.sjh.multiwatch.presentation.member.dto;


import com.sjh.multiwatch.domain.member.MemberRole;

public record RegisterMemberRequest (
        String email,
        String password,
        MemberRole role
){}
