package com.example.sftploader.scheduler;

import com.example.sftploader.repository.FileRecordRepository;
import com.example.sftploader.service.SftpSyncService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SftpScheduler {

    private final SftpSyncService service;
    private final FileRecordRepository repo;

    public SftpScheduler(SftpSyncService service, FileRecordRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        if (repo.count() == 0) {
            service.initialScan();
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void delta() {
        service.deltaScan();
    }
}
