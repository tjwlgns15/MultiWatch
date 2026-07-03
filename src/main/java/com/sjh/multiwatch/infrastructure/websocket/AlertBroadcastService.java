package com.sjh.multiwatch.infrastructure.websocket;

import com.sjh.multiwatch.domain.alert.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(Alert alert, Long deviceId) {
        String destination = "/topic/org/" + alert.getOrganizationId() + "/alerts";
        messagingTemplate.convertAndSend(destination, AlertBroadcastPayload.from(alert, deviceId));
    }
}
