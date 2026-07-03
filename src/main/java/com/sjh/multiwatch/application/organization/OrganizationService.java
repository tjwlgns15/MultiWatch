package com.sjh.multiwatch.application.organization;

import com.sjh.multiwatch.domain.member.Member;
import com.sjh.multiwatch.domain.member.MemberRepository;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.presentation.api.organization.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.DUPLICATE_EMAIL;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Organization signUp(SignUpRequest request) {
        validateEmailNotDuplicated(request.adminEmail());

        Organization organization = organizationRepository.save(Organization.register(request.organizationName()));

        Member admin = Member.registerAdmin(
                organization.getId(), request.adminEmail(), passwordEncoder.encode(request.adminPassword())
        );
        memberRepository.save(admin);

        return organization;
    }

    private void validateEmailNotDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
    }
}