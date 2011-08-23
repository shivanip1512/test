package com.cannontech.tools.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpWriter {
    private String username;
    private String password;
    private String host;
    private static Logger log = YukonLogManager.getLogger(SftpWriter.class);

    public SftpWriter(String username, String password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    public SftpStatus sendFile(File file) throws Exception {
        SftpStatus sendStatus = SftpStatus.SUCCESS;

        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(username, host, 22);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;

            try {
                sftpChannel.put(new FileInputStream(file), file.getName());
            } catch (FileNotFoundException e) {
                log.error("Problem trying to send file to SFTP server, file might have been removed before the send process started.",
                          e);
                sendStatus = SftpStatus.SEND_ERROR;
            }

            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            log.warn("Bad username or password", e);
            sendStatus = SftpStatus.BAD_USER_PASS;
        } catch (SftpException e) {
            log.error("Problem trying to send file to SFTP server", e);
            sendStatus = SftpStatus.SEND_ERROR;
        }

        return sendStatus;

    }
}
