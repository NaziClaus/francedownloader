package com.example.sftploader.service;

import com.example.sftploader.model.FileRecord;
import com.example.sftploader.repository.FileRecordRepository;
import com.example.sftploader.sftp.SftpClientWrapper;
import com.example.sftploader.sftp.SftpFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sftploader.report.CsvReportWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
public class SftpSyncService {

    private static final Logger logger = LoggerFactory.getLogger(SftpSyncService.class);

    private final SftpClientWrapper client;
    private final FileRecordRepository repo;
    private final Path downloadDir;
    private final CsvReportWriter csvWriter;

    public SftpSyncService(SftpClientWrapper client,
                           FileRecordRepository repo,
                           CsvReportWriter csvWriter,
                           @Value("${app.download-dir}") String downloadDir) {
        this.client = client;
        this.repo = repo;
        this.csvWriter = csvWriter;
        this.downloadDir = Path.of(downloadDir);
    }

    @Transactional
    public void initialScan() {
        logger.info("Starting initial scan");
        try {
            Files.createDirectories(downloadDir);
            List<SftpFileInfo> files = client.listFiles(client.getRemoteDir());
            for (SftpFileInfo info : files) {
                if (shouldHandle(info.filename())) {
                    repo.findByRemotePath(info.path()).orElseGet(() -> {
                        FileRecord record = new FileRecord();
                        record.setFilename(info.filename());
                        record.setRemotePath(info.path());
                        record.setSize(info.size());
                        record.setRemoteMtime(info.mtime());
                        return repo.save(record);
                    });
                }
            }
        } catch (Exception e) {
            logger.error("Initial scan failed", e);
        }
    }

    @Transactional
    public void deltaScan() {
        logger.info("Starting delta scan");
        try {
            Files.createDirectories(downloadDir);
            List<SftpFileInfo> files = client.listFiles(client.getRemoteDir());
            Instant now = Instant.now();
            for (SftpFileInfo info : files) {
                if (!shouldHandle(info.filename())) {
                    continue;
                }
                FileRecord record = repo.findByRemotePath(info.path())
                        .orElseGet(() -> {
                            FileRecord r = new FileRecord();
                            r.setFilename(info.filename());
                            r.setRemotePath(info.path());
                            return r;
                        });
                if (record.getRemoteMtime() == null || info.mtime().isAfter(record.getRemoteMtime())) {
                    Path tempPath = downloadDir.resolve(info.filename() + ".part");
                    Path finalPath = downloadDir.resolve(info.filename());
                    client.download(info.path(), tempPath);
                    Files.move(tempPath, finalPath);
                    record.setSize(info.size());
                    record.setRemoteMtime(info.mtime());
                    record.setDownloaded(true);
                    record.setLastDownloaded(now);
                    repo.save(record);
                    csvWriter.append(record);
                    logger.info("Downloaded {}", info.filename());
                }
            }
        } catch (Exception e) {
            logger.error("Delta scan failed", e);
        }
    }

    private boolean shouldHandle(String filename) {
        return filename.endsWith(".zip") || filename.endsWith(".rar");
    }
}
