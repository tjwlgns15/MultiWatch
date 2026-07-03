package com.sjh.multiwatch.presentation.api.member;

import com.sjh.multiwatch.application.member.MemberService;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.api.member.dto.MeResponse;
import com.sjh.multiwatch.presentation.api.member.dto.RegisterMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal MemberPrincipal principal) {
        MeResponse response = new MeResponse(
                principal.getMemberId(),
                principal.getOrganizationId(),
                principal.getEmail(),
                principal.getRole()
        );
        return ResponseEntity.ok(response);
    }
}