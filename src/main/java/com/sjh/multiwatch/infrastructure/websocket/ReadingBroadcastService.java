package com.sjh.multiwatch.infrastructure.websocket;

import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadingBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(ReadingMessage message) {
        String destination = "/topic/org/" + message.organizationId() + "/readings";
        messagingTemplate.convertAndSend(destination, ReadingBroadcastPayload.from(message));
    }
}
