package com.sjh.multiwatch.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.domain.device.DeviceType;
import com.sjh.multiwatch.domain.member.Member;
import com.sjh.multiwatch.domain.member.MemberRepository;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import com.sjh.multiwatch.presentation.api.alert.dto.RegisterAlertRuleRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 실제 로컬 MySQL 필요 (localhost:3308)
 */
@SpringBootTest
@AutoConfigureMockMvc
class TenantIsolationIntegrationTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired OrganizationRepository organizationRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired DeviceRepository deviceRepository;
    @Autowired AlertRuleRepository alertRuleRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Organization orgA;
    private Organization orgB;
    private Device deviceOfA;
    private Device deviceOfB;

    @BeforeEach
    void setUp() {
        orgA = organizationRepository.save(Organization.register("A사"));
        orgB = organizationRepository.save(Organization.register("B사"));

        memberRepository.save(Member.registerAdmin(orgA.getId(), "admin@a.com", passwordEncoder.encode("password")));

        deviceOfA = deviceRepository.save(Device.register(orgA.getId(), "device-a-1", "센서A", DeviceType.TEMPERATURE));
        deviceOfB = deviceRepository.save(Device.register(orgB.getId(), "device-b-1", "센서B", DeviceType.TEMPERATURE));
    }

    @AfterEach
    void tearDown() {
        alertRuleRepository.deleteAll();
        deviceRepository.deleteAll();
        memberRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("A사 로그인 사용자는 A사 디바이스만 조회할 수 있고 B사 디바이스는 결과에 포함되지 않는다")
    void deviceListIsIsolatedByTenant() throws Exception {
        MockHttpSession session = loginAsOrgAAdmin();

        MvcResult result = mockMvc.perform(get("/api/devices").session(session))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("device-a-1");
        assertThat(body).doesNotContain("device-b-1");
    }

    @Test
    @DisplayName("A사 사용자가 B사 소속 deviceId로 알림 규칙을 등록하면 404가 반환된다")
    void createAlertRuleForOtherOrganizationDeviceIsRejected() throws Exception {
        MockHttpSession session = loginAsOrgAAdmin();
        RegisterAlertRuleRequest request = new RegisterAlertRuleRequest(deviceOfB.getId(), 30.0, com.sjh.multiwatch.domain.alert.Comparator.GT);

        mockMvc.perform(post("/api/alert-rules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A사 사용자가 A사 소속 deviceId로 알림 규칙을 등록하면 성공한다")
    void createAlertRuleForOwnDeviceSucceeds() throws Exception {
        MockHttpSession session = loginAsOrgAAdmin();
        RegisterAlertRuleRequest request = new RegisterAlertRuleRequest(deviceOfA.getId(), 30.0, com.sjh.multiwatch.domain.alert.Comparator.GT);

        mockMvc.perform(post("/api/alert-rules")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증되지 않은 요청은 거부된다")
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession loginAsOrgAAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "admin@a.com", "password": "password"}
                            """)
                        .session(session))
                .andExpect(status().isOk());
        return session;
    }
}