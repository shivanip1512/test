package com.cannontech.web.stars.commChannel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PhysicalPort;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class CommChannelSetupHelper {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public void setupCommChannelFields(PortBase commChannel, ModelMap model) {
        model.addAttribute("baudRateList", BaudRate.values());
        if (commChannel instanceof TerminalServerPortDetailBase) {
            model.addAttribute("isAdditionalConfigSupported", true);
            model.addAttribute("isPortNumberSupported", true);
            if (commChannel instanceof UdpPortDetail) {
                model.addAttribute("isEncyptionSupported", true);
            }
            if (commChannel instanceof TcpSharedPortDetail) {
                model.addAttribute("isIpAddressSupported", true);
            }
        }
        if (commChannel instanceof LocalSharedPortDetail) {
            model.addAttribute("isAdditionalConfigSupported", true);
            model.addAttribute("isPhysicalPortSupported", true);
            List<String> physicalPortList = new ArrayList<>();
            for (PhysicalPort value : PhysicalPort.values()) {
                physicalPortList.add(value.getPhysicalPort());
            }
            model.addAttribute("physicalPortList", physicalPortList);
            model.addAttribute("otherPhysicalPort", PhysicalPort.OTHER.getPhysicalPort());
        }
    }

    public void setupGlobalError(BindingResult result, ModelMap model, YukonUserContext userContext, PaoType commChannelType) {
        if (result.hasGlobalErrors()) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            List<ObjectError> globalError = result.getGlobalErrors();
            List<String> uniqueErrorMsg = new ArrayList<>();
            for (ObjectError objectError : globalError) {
                uniqueErrorMsg.add(accessor.getMessage(objectError.getCode(), objectError.getArguments()));
            }
            if (PaoType.TSERVER_SHARED == commChannelType) {
                result.rejectValue("ipAddress", "yukon.common.blank");
            }
            result.rejectValue("portNumber", "yukon.common.blank");
            model.addAttribute("uniqueErrorMsg", uniqueErrorMsg);
        }
    }

    public void setupPhysicalPort(PortBase commChannel, ModelMap model) {
        if (commChannel instanceof LocalSharedPortDetail) {
            if (PhysicalPort
                    .getByDbString(((LocalSharedPortDetail) commChannel).getPhysicalPort()) == PhysicalPort.OTHER) {
                model.addAttribute("isPhysicalPortUserDefined", true);
            }
        }
    }
}
