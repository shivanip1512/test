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
    
    //TODO: remove after testing
    private static final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\r\n" + 
            "MIICXAIBAAKBgQCH8jSyTs8ZwCOIWDEmkmzVrX+u94+eAwJxiMIill3kUGzR32bk\r\n" + 
            "cHoHtz7wQfNXo3f58xWNf6/I0vf7DtvlDzTtfjyTAWSyarPyVfBHgyWI/q9xEk0p\r\n" + 
            "hUNmYR2v4W/DeYdbsQaDV4UFlFMxkV0fK79A47XsHUYcDZbU+xuvsKKX0QIDAQAB\r\n" + 
            "AoGAWTgxM3CbZLf/2eSfZUQl31p9eNQKUlaBTQfG+TIcLtJFiLuIaJYU0GmICeAO\r\n" + 
            "l062V7kcRQAu2qryircmw77mKZ6QjFDTnp1Gkiaj5oT0+xh7en4NQ0GkYR4RLeEz\r\n" + 
            "AZCV0borvRs/ZvVsCt6O5N4HQXn1j8Y9Q7dntHHdyRcO7h0CQQC9gP7mmLPk5je1\r\n" + 
            "zEmsuz6NFa8fRBMQwHK+tWZqt+dBvWvMTrDhD7YmcV2KRCYQZ5um2ltDP1LqB+wn\r\n" + 
            "sWDVI6hTAkEAt6Ym9I5Mqqm9qm5laJeuH+Bv9r00Svsa/4ATyWK/nwR1/cRhDdZn\r\n" + 
            "SScNxqzFDq0Ed8grgjwaijol/yqzG3eqywJAAXaPgURIj0nfwD2VjAneD8TNF1a1\r\n" + 
            "NvnlaOeJpOVKmc1Wmbs5zmMKqv1RFZI1IQdLwlwJPFmu0DPDdQL/lmWyFQJAdyuD\r\n" + 
            "NcitKUWSROj0NyXOIPND1Em8iDdfspJKxLCmhaqgYjavgAoz5b1I7DYqfTZ6oruq\r\n" + 
            "uA0Gb9IB3A26QcWqdwJBAITVwXuxfHCd8Wf//xMXIhUYGtJVTRSO9Ik4GnY5T7N6\r\n" + 
            "7XcVkchFa4SSog9TZ5dgVdf6xRVH0aqeDcubisPaHyw=\r\n" + 
            "-----END RSA PRIVATE KEY-----";
    
    @GetMapping("sftpTest")
    public String sftpTest(ModelMap model) {
        SftpTest sftp = new SftpTest();
        sftp.setDomain("test.rebex.net");
        sftp.setPort("22");
        sftp.setSftpPath("readme.txt");
        sftp.setFilename("C:\\Yukon\\readme-copy.txt");
        sftp.setUsername("demo");
        sftp.setPassword("password");
        model.addAttribute("sftp", sftp);
        return "sftpTest/sftpTest.jsp";
    }
    
    @PostMapping("execute")
    public String execute(@ModelAttribute("sftp") SftpTest sftp, FlashScope flash) {
        
        try (
            //TODO: get publicKey from the database once YUK-20458 is done
            SftpConnection sftpService = new SftpConnection(sftp.getDomain(), sftp.getPort(), 
                                                      YukonHttpProxy.fromGlobalSetting(globalSettingDao),
                                                      sftp.getUsername(), sftp.getPassword(),
                                                      privateKey);
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
