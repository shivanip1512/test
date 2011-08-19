package com.cannontech.tools.sftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

import com.cannontech.common.util.FileUtil;

public class SftpWriter {
    private String username;
    private String password;
    private String ftpDirectory;
    private String host;

    public SftpWriter(String username, String password, String ftpDirectory, String host) {
        this.ftpDirectory = ftpDirectory;
        this.username = username;
        this.password = password;
        this.host = host;
    }
    
    public SftpStatus sendFile(File file) throws Exception {
        SftpStatus sendStatus = SftpStatus.SUCCESS;
        
        FileSystemOptions fsOptions = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(fsOptions, "no");
        DefaultFileSystemManager fsManager = (DefaultFileSystemManager)VFS.getManager();
        
        String uri = "sftp://" + username + ":" + password + "@" + host + "/";
        if (StringUtils.isEmpty(ftpDirectory)) {
            uri += file.getName();
        } else {
            uri += ftpDirectory +"/" + file.getName();
        }
        
        FileObject fo = null;
        
        try {
            fo = fsManager.resolveFile(uri, fsOptions);
            OutputStream out = new BufferedOutputStream(fo.getContent().getOutputStream());
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            
            FileUtil.copyNoFlush(in, out);
            
            out.close();
            fo.close();
        } catch (FileSystemException e) {
            sendStatus = SftpStatus.SEND_ERROR;
        }

        return sendStatus;
    }

    
}
