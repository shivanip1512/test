package com.cannontech.web.dev;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.SftpConnection;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.model.SftpTest;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/sftpTest/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SftpTestController {
    private static final Logger log = YukonLogManager.getLogger(SftpTestController.class);
    @Autowired GlobalSettingDao globalSettingDao;
    
    @GetMapping("sftpTest")
    public String sftpTest(ModelMap model) {
        SftpTest sftp = new SftpTest();
        sftp.setDomain("test.rebex.net");
        sftp.setPort("22");
        sftp.setSftpPath("readme.txt");
        sftp.setFilename("C:\\Yukon\\readme-copy.txt");
        sftp.setUsername("demo");
        sftp.setPassword("password");
        sftp.setPrivateKey("");
        model.addAttribute("sftp", sftp);
        return "sftpTest/sftpTest.jsp";
    }
    
    @PostMapping("execute")
    public String execute(@ModelAttribute("sftp") SftpTest sftp, FlashScope flash) {
        
        try (
            SftpConnection sftpService = new SftpConnection(sftp.getDomain(), sftp.getPort(), 
                                                      YukonHttpProxy.fromGlobalSetting(globalSettingDao),
                                                      sftp.getUsername(), sftp.getPassword(), sftp.getPrivateKey());
        ) {
            sftpService.copyRemoteFile(sftp.getSftpPath(), sftp.getFilename());
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Copied " + sftp.getDomain() + "//" 
                    + sftp.getSftpPath() + " to " + sftp.getFilename()));
        } catch(Exception e) {
            log.error("An error occurred in SFTP file transfer", e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.sftp.error", e.getMessage()));
        }
        
        return "sftpTest/sftpTest.jsp";
    }
}
