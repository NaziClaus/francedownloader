package com.example.sftploader.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class SftpClientWrapper {

    private static final Logger logger = LoggerFactory.getLogger(SftpClientWrapper.class);

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port:22}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.remote-dir}")
    private String remoteDir;

    private ChannelSftp setup() throws Exception {
        var jsch = new JSch();
        var session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        var channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 30000))
    public List<SftpFileInfo> listFiles(String path) throws Exception {
        var sftp = setup();
        try {
            var files = sftp.ls(path);
            List<SftpFileInfo> result = new ArrayList<>();
            for (LsEntry entry : files) {
                if (!entry.getAttrs().isDir()) {
                    result.add(new SftpFileInfo(
                            entry.getFilename(),
                            path + "/" + entry.getFilename(),
                            entry.getAttrs().getSize(),
                            Instant.ofEpochSecond(entry.getAttrs().getMTime())
                    ));
                }
            }
            return result;
        } finally {
            sftp.disconnect();
            sftp.getSession().disconnect();
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 30000))
    public void download(String remote, Path local) throws Exception {
        var sftp = setup();
        try (InputStream in = sftp.get(remote)) {
            Files.copy(in, local);
        } finally {
            sftp.disconnect();
            sftp.getSession().disconnect();
        }
    }

    @Recover
    public List<SftpFileInfo> recoverList(Exception e, String path) {
        logger.error("Failed to list files from {}", path, e);
        return List.of();
    }

    @Recover
    public void recoverDownload(Exception e, String remote, Path local) {
        logger.error("Failed to download {}", remote, e);
    }

    public String getRemoteDir() {
        return remoteDir;
    }
}
