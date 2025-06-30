package com.example.sftploader.report;

import com.example.sftploader.model.FileRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class CsvReportWriter {

    private static final Logger logger = LoggerFactory.getLogger(CsvReportWriter.class);

    private final Path csvPath;

    public CsvReportWriter(@Value("${app.csv-file}") String csvFile) {
        this.csvPath = Path.of(csvFile);
    }

    public synchronized void append(FileRecord record) {
        try {
            Files.createDirectories(csvPath.getParent());
            boolean newFile = Files.notExists(csvPath);
            try (BufferedWriter writer = Files.newBufferedWriter(csvPath,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (newFile) {
                    writer.write("filename,remotePath,size,remoteMtime,lastDownloaded");
                    writer.newLine();
                }
                writer.write(String.format("%s,%s,%d,%s,%s",
                        record.getFilename(),
                        record.getRemotePath(),
                        record.getSize(),
                        record.getRemoteMtime(),
                        record.getLastDownloaded()));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Failed to write CSV record", e);
        }
    }
}
