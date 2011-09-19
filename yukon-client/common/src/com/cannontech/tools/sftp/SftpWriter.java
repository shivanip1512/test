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
    public static enum Status {
        INVALID_CREDENTIALS,
        SEND_ERROR, 
        SUCCESS;
    }

    private String username;
    private String password;
    private String host;
    private static Logger log = YukonLogManager.getLogger(SftpWriter.class);

    public SftpWriter(String username, String password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    public Status sendFile(File file) {
        Status sendStatus = Status.SUCCESS;

        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp sftpChannel = null;
        try {
            session = jsch.getSession(username, host, 22);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

            sftpChannel = (ChannelSftp) channel;

            try {
                sftpChannel.put(new FileInputStream(file), file.getName());
            } catch (FileNotFoundException fnfe) {
                log.error("Problem trying to send file to SFTP server, file might have been " +
                		"removed before the send process started.", fnfe);
                sendStatus = Status.SEND_ERROR;
            }
        }
        catch (JSchException e) {
            log.warn("Bad username or password", e);
            sendStatus = Status.INVALID_CREDENTIALS;
        } 
        catch (SftpException e) {
            log.error("Problem trying to send file to SFTP server", e);
            sendStatus = Status.SEND_ERROR;
        }
        finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }

        return sendStatus;
    }
}
