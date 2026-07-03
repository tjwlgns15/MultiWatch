package com.sjh.multiwatch.presentation.api.device;

import com.sjh.multiwatch.application.device.DeviceService;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.api.device.dto.DeviceResponse;
import com.sjh.multiwatch.presentation.api.device.dto.RegisterDeviceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Long> register(
            @RequestBody RegisterDeviceRequest request,
            @AuthenticationPrincipal MemberPrincipal principal) {

        Long deviceId = deviceService.registerDevice(request, principal.getOrganizationId());
        return ResponseEntity.ok(deviceId);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> list() {
        List<DeviceResponse> devices = deviceService.getDevices().stream()
                .map(DeviceResponse::from)
                .toList();
        return ResponseEntity.ok(devices);
    }

}
