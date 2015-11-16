package com.cannontech.web.stars.dr.operator.hardware;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareReadServiceImpl;

@Controller
@RequestMapping("/operator/hardware/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class HardwareController {

  @Autowired private HardwareReadServiceImpl hardwareServiceImpl;
  
  @RequestMapping("/readNow")
  @ResponseBody
  public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
      Map<String, Object> resultMap = new HashMap<>();
      
      resultMap = hardwareServiceImpl.readNow(deviceId, userContext);
      return resultMap;
  }
}