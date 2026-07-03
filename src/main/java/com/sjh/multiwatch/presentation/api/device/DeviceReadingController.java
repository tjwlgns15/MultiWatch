package com.sjh.multiwatch.presentation.api.device;

import com.sjh.multiwatch.application.device.ReadingQueryService;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.api.device.dto.DeviceReadingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices/{deviceId}/readings")
@RequiredArgsConstructor
public class DeviceReadingController {

    private final ReadingQueryService readingQueryService;

    @GetMapping
    public ResponseEntity<List<DeviceReadingResponse>> list(
            @PathVariable Long deviceId,
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<DeviceReadingResponse> responses = readingQueryService.getRecentReadings(deviceId, principal.getOrganizationId(), limit).stream()
                .map(DeviceReadingResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}