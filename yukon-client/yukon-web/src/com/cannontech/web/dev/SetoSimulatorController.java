package com.cannontech.web.dev;

import java.util.Optional;

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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.jms.ThriftRequestTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.edgeDr.EdgeDrDataNotification;
import com.cannontech.dr.edgeDr.EdgeDrError;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr.EdgeDrDataNotificationSerializer;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/seto/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class SetoSimulatorController {
    private final Logger log = YukonLogManager.getLogger(SetoSimulatorController.class);

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private IDatabaseCache iDatabaseCache;

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
        Optional<LiteYukonPAObject> optionalMeter= iDatabaseCache.getAllYukonPAObjects().stream().filter(pao -> pao.getPaoType() == PaoType.RFN530S4X_DER).findAny();
        LiteYukonPAObject theChosenMeter = optionalMeter.orElseGet(() -> iDatabaseCache.getAllYukonPAObjects().get(0));
        EdgeDrDataNotification edgeDrDataNotification = new EdgeDrDataNotification(theChosenMeter.getPaoIdentifier().getPaoId(), null, 1, new EdgeDrError(24, "SETO Test Error"));
        model.addAttribute("url", url);
        model.addAttribute("edgeDrDataNotification", edgeDrDataNotification);
        return "setoSimulator.jsp";
    }

    @PostMapping("/sendEdgeDrDataNotification")
    public String sendPorterMessageToCC(@ModelAttribute("edgeDrDataNotification") EdgeDrDataNotification edgeDrDataNotification,
            FlashScope flash) {
        final String homeKey = "yukon.web.modules.dev.setoSimulator.";
        log.info("Received EdgeDrDataNotification Meessage {} from Seto Simulator", edgeDrDataNotification);
        //Probably need to do some basic validation?
        thriftDataNotification.send(edgeDrDataNotification);
        flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "send.success"));
        log.info("Sent porter message {} to DER Edge Response Service", edgeDrDataNotification);
        return "redirect:setoSimulator";
    }
}
