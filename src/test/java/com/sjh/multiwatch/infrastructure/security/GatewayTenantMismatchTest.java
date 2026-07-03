package com.sjh.multiwatch.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.domain.device.DeviceType;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import com.sjh.multiwatch.presentation.api.device.dto.IngestReadingRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 실제 로컬 MySQL 필요 (localhost:3308)
 */
@SpringBootTest
@AutoConfigureMockMvc
class GatewayTenantMismatchTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired MockMvc mockMvc;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired DeviceRepository deviceRepository;

    private Organization orgA;
    private Organization orgB;
    private Device deviceOfA;
    private Device deviceOfB;

    @BeforeEach
    void setUp() {
        orgA = organizationRepository.save(Organization.register("A사"));
        orgB = organizationRepository.save(Organization.register("B사"));

        deviceOfA = deviceRepository.save(Device.register(orgA.getId(), "device-a-1", "센서A", DeviceType.TEMPERATURE));
        deviceOfB = deviceRepository.save(Device.register(orgB.getId(), "device-b-1", "센서B", DeviceType.TEMPERATURE));
    }

    @AfterEach
    void tearDown() {
        deviceRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("A사 API Key로 A사 소속 디바이스에 데이터를 전송하면 202가 반환된다")
    void ingestForOwnDeviceSucceeds() throws Exception {
        List<IngestReadingRequest> requests = List.of(
                new IngestReadingRequest(deviceOfA.getDeviceKey(), 25.5, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/gateway/readings")
                        .header("X-API-KEY", orgA.getApiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("A사 API Key로 B사 소속 디바이스에 데이터를 전송하면 404가 반환된다")
    void rejectsReadingForDeviceNotBelongingToRequestingOrganization() throws Exception {
        List<IngestReadingRequest> requests = List.of(
                new IngestReadingRequest(deviceOfB.getDeviceKey(), 25.5, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/gateway/readings")
                        .header("X-API-KEY", orgA.getApiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효하지 않은 API Key로 요청하면 403이 반환된다")
    void rejectsRequestWithInvalidApiKey() throws Exception {
        List<IngestReadingRequest> requests = List.of(
                new IngestReadingRequest(deviceOfA.getDeviceKey(), 25.5, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/gateway/readings")
                        .header("X-API-KEY", "invalid-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("API Key 없이 요청하면 401이 반환된다")
    void rejectsRequestWithoutApiKey() throws Exception {
        List<IngestReadingRequest> requests = List.of(
                new IngestReadingRequest(deviceOfA.getDeviceKey(), 25.5, LocalDateTime.now())
        );

        mockMvc.perform(post("/api/gateway/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isUnauthorized());
    }
}