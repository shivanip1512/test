package com.cannontech.web.dev;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dev.model.SftpTest;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/sftpTest/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SftpTestController {
    
    @GetMapping("sftpTest")
    public String sftpTest(ModelMap model, YukonUserContext userContext) {
        SftpTest sftp = new SftpTest();
        sftp.setDomain("demo.wftpserver.com");
        sftp.setPort("2222");
        sftp.setSftpPath("/download/manual_en.pdf");
        sftp.setFilename("thefile1.pdf");
        sftp.setUsername("demo-user");
        sftp.setPassword("demo-user");
        model.addAttribute("sftp", sftp);
        return "sftpTest/sftpTest.jsp";
    }
    
    @PostMapping("execute")
    public String execute(@ModelAttribute("sftp") SftpTest sftp, ModelMap model, YukonUserContext userContext) {
        FileSystemManager manager;
        String domainPort = sftp.getDomain() + ":" + sftp.getPort();
        String sftpFileLocation = "sftp://" + domainPort + sftp.getSftpPath();
        try {
            
            StaticUserAuthenticator auth = new StaticUserAuthenticator(domainPort, sftp.getUsername(), sftp.getPassword());// domain/username/password
            FileSystemOptions opts = new FileSystemOptions();
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
            manager = VFS.getManager();

            FileObject remoteFile = manager.resolveFile(sftpFileLocation, opts);
            FileObject localFile = manager.resolveFile("C:\\Yukon\\" + sftp.getFilename());
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
            } catch (Exception e) {
            System.out.println(e);    
        }
        return "sftpTest/sftpTest.jsp";
    }
}
