package com.cannontech.web.support.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.security.annotation.AuthorizeByCparm;

@Controller
@RequestMapping("/development/pickerTest/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class PickerTestController {
    @RequestMapping("main")
    public void main() {
    }
}
