package com.cannontech.web.dev;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.util.jms.ThriftRequestTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.edgeDr.EdgeDrDataNotification;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrDataNotificationSerializer;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/seto/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SetoSimulatorController {
    private final Logger log = YukonLogManager.getLogger(SetoSimulatorController.class);

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ConfigurationSource configurationSource;

    private ThriftRequestTemplate<EdgeDrDataNotification> thriftDataNotification;

    @PostConstruct
    public void initialize() {
        thriftDataNotification = new ThriftRequestTemplate<>(
                jmsTemplateFactory.createTemplate(JmsApiDirectory.EDGE_DR_DATA),
                new EdgeDrDataNotificationSerializer());
    }

    @GetMapping("setoSimulator")
    public String itronSimulator(ModelMap model) {
        String url = configurationSource.getString(MasterConfigString.SETO_WEBHOOK_URL);
        // Use to populate random starting values
        EdgeDrDataNotification edgeDrDataNotification = new EdgeDrDataNotification(0, null, null, null);
        model.addAttribute("url", url);
        model.addAttribute("edgeDrDataNotification", edgeDrDataNotification);
        return "setoSimulator.jsp";
    }

    @PostMapping("/sendPorterMessageToCC")
    public String sendPorterMessageToCC(@ModelAttribute("edgeDrDataNotification") EdgeDrDataNotification edgeDrDataNotification,
            FlashScope flash) {
        final String homeKey = "yukon.web.modules.dev.setoSimulator.";
        log.info("Recieved EdgeDrDataNotification Meessage {} from Seto Simulator", edgeDrDataNotification);
        //Probably need to do some basic validation?
        thriftDataNotification.send(edgeDrDataNotification);
        flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "send.success"));
        log.info("Sent porter message {} to Central Controller", edgeDrDataNotification);
        return "redirect:setoSimulator";
    }
}
