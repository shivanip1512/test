package com.cannontech.web.api.captcha;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.api.captcha.model.CaptchaDetails;
import com.cannontech.web.common.captcha.service.CaptchaService;

@RestController
public class CaptchaDetailsApiController {

    @Autowired private CaptchaService captchaService;
    @Autowired private GlobalSettingDao globalSettingDao;

    @GetMapping("/captchaDetails")
    public ResponseEntity<Object> getCaptchaDetails(HttpServletRequest request) {

        CaptchaDetails captchaDetails = new CaptchaDetails();
        captchaDetails.setSiteKey(captchaService.getSiteKey());
        captchaDetails.setCaptchaEnabled(globalSettingDao.getBoolean(GlobalSettingType.ENABLE_CAPTCHAS));
        captchaDetails.setLocale(RequestContextUtils.getLocale(request).getLanguage());

        return new ResponseEntity<>(captchaDetails, HttpStatus.OK);
    }

}
