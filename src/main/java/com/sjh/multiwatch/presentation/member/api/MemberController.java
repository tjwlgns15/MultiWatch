package com.sjh.multiwatch.presentation.member.api;

import com.sjh.multiwatch.application.member.MemberService;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.member.dto.RegisterMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Long> register(
            @RequestBody RegisterMemberRequest request,
            @AuthenticationPrincipal MemberPrincipal principal) {

        Long memberId = memberService.registerMember(request, principal);
        return ResponseEntity.ok(memberId);
    }
}