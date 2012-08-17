package com.cannontech.web.stars.dr.operator.hardware;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.dr.rfn.service.WaitableRfnUnicastCallback;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/operator/hardware/rf/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class RfHardwareController {

    private static final Logger log = YukonLogManager.getLogger(RfHardwareController.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping
    public void readNow(HttpServletResponse resp, YukonUserContext context, int deviceId) throws IOException {
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        final RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        final JSONObject json = new JSONObject();
        
        /* Using a waitable, this will block for the initial response to the read request or until the intial response timeout expires. */
        WaitableRfnUnicastCallback waitableCallback = new WaitableRfnUnicastCallback(new RfnUnicastCallback() {

            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                log.error(message);
                json.put("success", false);
                json.put("message", accessor.getMessage(message));
            }
            
            @Override
            public void receivedStatus(RfnExpressComUnicastReplyType status) {
                boolean success = status == RfnExpressComUnicastReplyType.OK ? true : false;
                json.put("success", success);
                if (success) {
                    log.debug("Read now initiated for " + device);
                    json.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
                } else {
                    log.debug("Read now failed for " + device);
                    json.put("message", accessor.getMessage(keyBase + "error.readNowFailed", status));
                }
            }

            @Override 
            public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                log.error("Error Reading RF Hardware: " + replyType);
                json.put("success", false);
                json.put("message", accessor.getMessage(keyBase + "error.readNowFailed", replyType));
            }
            
            @Override public void complete() {}
        });
            
        rfnExpressComMessageService.readDevice(device, waitableCallback);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {/* ignore */};
        
        resp.setContentType("application/json");
        resp.getWriter().print(json.toString());
        resp.getWriter().close();
    }
    
}