package com.example.sftploader.sftp;

import java.time.Instant;

public class SftpFileInfo {
    private String filename;
    private String path;
    private long size;
    private Instant mtime;

    public SftpFileInfo(String filename, String path, long size, Instant mtime) {
        this.filename = filename;
        this.path = path;
        this.size = size;
        this.mtime = mtime;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public Instant getMtime() {
        return mtime;
    }
}
