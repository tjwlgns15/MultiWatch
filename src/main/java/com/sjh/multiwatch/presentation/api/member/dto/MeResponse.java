package com.sjh.multiwatch.presentation.api.member.dto;

import com.sjh.multiwatch.domain.member.MemberRole;

public record MeResponse(
        Long memberId,
        Long organizationId,
        String email,
        MemberRole role
) {}