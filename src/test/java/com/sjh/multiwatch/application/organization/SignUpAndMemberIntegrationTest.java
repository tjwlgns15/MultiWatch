package com.sjh.multiwatch.application.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjh.multiwatch.domain.member.Member;
import com.sjh.multiwatch.domain.member.MemberRepository;
import com.sjh.multiwatch.domain.member.MemberRole;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import com.sjh.multiwatch.presentation.member.dto.RegisterMemberRequest;
import com.sjh.multiwatch.presentation.organization.dto.SignUpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 실제 로컬 MySQL 필요 (localhost:3308)
 */
@SpringBootTest
@AutoConfigureMockMvc
class SignUpAndMemberIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired MockMvc mockMvc;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("조직 가입 시 조직과 최초 관리자 계정이 함께 생성된다")
    void signUp_createsOrganizationAndAdmin() throws Exception {
        SignUpRequest request = new SignUpRequest("신규조직", "owner@new.com", "password");

        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(memberRepository.existsByEmail("owner@new.com")).isTrue();
    }

    @Test
    @DisplayName("이미 사용 중인 이메일로 가입하면 409가 반환된다")
    void signUp_duplicateEmail_returnsConflict() throws Exception {
        Organization organization = organizationRepository.save(Organization.register("기존조직"));
        memberRepository.save(Member.registerAdmin(organization.getId(), "existing@a.com", passwordEncoder.encode("password")));

        SignUpRequest request = new SignUpRequest("신규조직", "existing@a.com", "password");

        mockMvc.perform(post("/api/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("관리자가 팀원을 추가하면 성공한다")
    void registerMember_byAdmin_succeeds() throws Exception {
        Organization organization = organizationRepository.save(Organization.register("A사"));
        memberRepository.save(Member.registerAdmin(organization.getId(), "admin@a.com", passwordEncoder.encode("password")));
        MockHttpSession session = login("admin@a.com", "password");

        RegisterMemberRequest request = new RegisterMemberRequest("viewer@a.com", "password", MemberRole.VIEWER);

        mockMvc.perform(post("/api/members")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(memberRepository.existsByEmail("viewer@a.com")).isTrue();
    }

    @Test
    @DisplayName("일반 팀원(VIEWER)이 팀원을 추가하려 하면 403이 반환된다")
    void registerMember_byViewer_forbidden() throws Exception {
        Organization organization = organizationRepository.save(Organization.register("A사"));
        memberRepository.save(Member.registerViewer(organization.getId(), "viewer@a.com", passwordEncoder.encode("password")));
        MockHttpSession session = login("viewer@a.com", "password");

        RegisterMemberRequest request = new RegisterMemberRequest("another@a.com", "password", MemberRole.VIEWER);

        mockMvc.perform(post("/api/members")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인하지 않고 팀원을 추가하려 하면 401이 반환된다")
    void registerMember_withoutAuthentication_unauthorized() throws Exception {
        RegisterMemberRequest request = new RegisterMemberRequest("another@a.com", "password", MemberRole.VIEWER);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession login(String email, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "%s", "password": "%s"}
                            """.formatted(email, password))
                        .session(session))
                .andExpect(status().isOk());
        return session;
    }
}