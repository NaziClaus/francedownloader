package com.example.sftploader.sftp;

import java.time.Instant;

public record SftpFileInfo(String filename, String path, long size, Instant mtime) {}
