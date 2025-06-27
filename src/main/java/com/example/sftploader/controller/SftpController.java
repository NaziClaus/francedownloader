package com.example.sftploader.controller;

import com.example.sftploader.service.SftpSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sftp")
public class SftpController {

    private final SftpSyncService service;

    public SftpController(SftpSyncService service) {
        this.service = service;
    }

    @PostMapping("/scan")
    public ResponseEntity<Void> triggerScan() {
        service.deltaScan();
        return ResponseEntity.ok().build();
    }
}
