package com.sjh.multiwatch.application.member;

import com.sjh.multiwatch.domain.member.Member;
import com.sjh.multiwatch.domain.member.MemberRepository;
import com.sjh.multiwatch.domain.member.MemberRole;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.member.dto.RegisterMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.MEMBER_FORBIDDEN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long registerMember(RegisterMemberRequest request, MemberPrincipal requester) {
        validateRequesterIsAdmin(requester);
        validateEmailNotDuplicated(request.email());

        Member member = createMember(request, requester.getOrganizationId());
        return memberRepository.save(member).getId();
    }

    private Member createMember(RegisterMemberRequest request, Long organizationId) {
        String passwordHash = passwordEncoder.encode(request.password());
        return request.role() == MemberRole.ADMIN
                ? Member.registerAdmin(organizationId, request.email(), passwordHash)
                : Member.registerViewer(organizationId, request.email(), passwordHash);
    }

    private void validateRequesterIsAdmin(MemberPrincipal requester) {
        if (requester.getRole() != MemberRole.ADMIN) {
            throw new CustomException(MEMBER_FORBIDDEN);
        }
    }

    private void validateEmailNotDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }
}
