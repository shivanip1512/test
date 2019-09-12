package com.cannontech.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Provides a simple API for setting up an SFTP connection and performing file operations over that connection.
 * SFTP is the SSH File Transfer Protocol (not to be confused with FTPS - secure FTP with TLS).
 * @see <a href="https://en.wikipedia.org/wiki/SSH_File_Transfer_Protocol">SSH File Transfer Protocol</a>
 */
public class SftpConnection implements AutoCloseable {
    private static final Logger log = YukonLogManager.getLogger(SftpConnection.class);
    private final FileSystemManager manager = VFS.getManager();
    private final String domainPort;
    private final FileSystemOptions fsOptions = new FileSystemOptions();
    private final String username;
    private final String password;
    private File privateKeyFile;
    private File publicKeyFile;
    
    public SftpConnection(String domain, String port, Optional<YukonHttpProxy> proxy, String username, String password, 
                       String privateKey) throws IOException {
        
        // Save the connection info
        if (StringUtils.isEmpty(port)) {
            domainPort = domain;
        } else {
            domainPort = domain + ":" + port;
        }
        this.username = username;
        this.password = password;
        
        SftpFileSystemConfigBuilder fscBuilder = SftpFileSystemConfigBuilder.getInstance();
        
        // Handle proxy setting configuration
        proxy.ifPresent(p -> {
            fscBuilder.setProxyHost(fsOptions, p.getHost());
            fscBuilder.setProxyPort(fsOptions, p.getPort());
            fscBuilder.setProxyType(fsOptions, SftpFileSystemConfigBuilder.PROXY_HTTP);
        });
        
        // Handle public/private authentication key configuration
        if (StringUtils.isNotEmpty(privateKey)) {
            String randomId = UUID.randomUUID().toString();
            privateKeyFile = createTempFile("sftpPrivate-" + randomId, privateKey);
            // Password-protected private key only seems to be supported via the VFS 2.4+ syntax. e.g.s
            // IdentityInfo identity = new IdentityInfo(privateKeyFile, publicKeyFile, password.getBytes());
            // fscBuilder.setIdentityProvider(fsOptions, identity);
            fscBuilder.setIdentities(fsOptions, new File[] {privateKeyFile});
        }
        
        fscBuilder.setUserDirIsRoot(fsOptions, true);
    }
    
    /**
     * Copy a file from the SFTP server to the local machine.
     * @param remoteSftpPath The relative path to the file on the SFTP server, using the SFTP user's home directory as 
     * the base.
     * @param localFilePath The place to copy the file on the local system, using an absolute file path.
     * @throws FileSystemException If an error occurs resolving or copying the file from the SFTP server
     */
    public void copyRemoteFile(String remoteSftpPath, String localFilePath) throws FileSystemException {
        log.info("File System Options - " + fsOptions.toString());
        
        // The "//" in the file path denotes the user home directory on the specified SFTP server
        // e.g. "sftp://demo:password@test.rebex.net//readme.txt"
        String fullSftpFilePath = "sftp://" + username + ":" + password + "@" + domainPort + "//" + remoteSftpPath;
        
        // Find the remote file on the SFTP server, and the local file on the local file system
        FileObject remoteFile = manager.resolveFile(fullSftpFilePath, fsOptions);
        FileObject localFile = manager.resolveFile(localFilePath);
        
        // Perform the copy
        log.info("Copying from " + remoteFile.getURL() + " to " + localFile.getURL());
        localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
    }
    
    @Override
    public void close() {
        try {
            Files.delete(publicKeyFile.toPath());
            Files.delete(privateKeyFile.toPath());
        } catch (Exception e) {
            log.debug("Error deleting key file", e);
        }
    }
    
    /**
     * Create a temp file containing the specified String, which will be deleted on JVM exit.
     */
    private File createTempFile(String fileName, String content) throws IOException {
        // Create the temp file
        File tempFile = File.createTempFile(fileName, null); //default extension: .tmp
        tempFile.deleteOnExit();
        log.info("Created temp file: " + tempFile.getCanonicalPath());
        
        // Write the content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }
        
        return tempFile;
    }
}
