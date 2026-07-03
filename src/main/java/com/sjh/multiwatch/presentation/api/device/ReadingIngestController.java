package com.sjh.multiwatch.presentation.api.device;

import com.sjh.multiwatch.application.device.ReadingIngestService;
import com.sjh.multiwatch.presentation.api.device.dto.IngestReadingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gateway/readings")
@RequiredArgsConstructor
public class ReadingIngestController {

    private final ReadingIngestService readingIngestService;

    @PostMapping
    public ResponseEntity<Void> ingest(@RequestBody List<IngestReadingRequest> requests) {
        readingIngestService.ingest(requests);
        return ResponseEntity.accepted().build();
    }
}
