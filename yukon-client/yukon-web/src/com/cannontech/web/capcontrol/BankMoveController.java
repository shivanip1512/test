package com.cannontech.web.capcontrol;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.model.BankMoveBean;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.MovedBank;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/move/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class BankMoveController {

    @Autowired private FilterCacheFactory cacheFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;

    @RequestMapping("bankMove")
    public String bankMove(ModelMap model, YukonUserContext context, int bankid) throws JsonProcessingException {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(context.getYukonUser());
        int subBusId = 0;
        JsTreeNode areaJson = capControlWebUtilsService.buildSimpleHierarchy();
        model.addAttribute("areaJson", JsonUtils.toJson(areaJson.toMap(), true));
        CapBankDevice capBank = filterCapControlCache.getCapBankDevice(bankid);

        int oldFeederId = 0;
        if (capBank != null) {
            oldFeederId = capBank.getParentID();
        }
        model.addAttribute("bankId", bankid);
        model.addAttribute("oldFeederId", oldFeederId);
        model.addAttribute("subBusId", subBusId);

        Feeder feeder = filterCapControlCache.getFeeder(capBank.getParentID());
        SubBus subBus = filterCapControlCache.getSubBus(feeder.getParentID());
        SubStation substation = filterCapControlCache.getSubstation(subBus.getParentID());
        StreamableCapObject area = filterCapControlCache.getStreamableArea(substation.getParentID());

        model.addAttribute("substationId", substation.getCcId());

        String path = accessor.getMessage("yukon.web.modules.capcontrol.bankMove.path",
                                          area.getCcName(),
                                          substation.getCcName(),
                                          subBus.getCcName(),
                                          feeder.getCcName());

        model.addAttribute("path", path);
        model.addAttribute("bankName", capBank.getCcName());
        model.addAttribute("controlType", CapControlType.CAPBANK);
        model.addAttribute("commandId", CommandType.MOVE_BANK.getCommandId());
        model.addAttribute("isIVVC", feeder.isIvvcControlled() || subBus.isIvvcControlled());

        BankMoveBean bankMoveBean = new BankMoveBean();
        bankMoveBean.setBankId(capBank.getCcId());
        bankMoveBean.setOldFeederId(capBank.getParentID());
        model.addAttribute("bankMoveBean", bankMoveBean);

        return "move/bankMove.jsp";
    }

    @RequestMapping("feederBankInfo")
    public String feederBankInfo(ModelMap model, LiteYukonUser user, int feederId) {

        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        Feeder feederobj = filterCapControlCache.getFeeder(feederId);
        String feederName = feederobj.getCcName();
        List<CapBankDevice> capBankList = filterCapControlCache.getCapBanksByFeeder(feederId);

        model.addAttribute("feederName", feederName);
        model.addAttribute("capBankList", capBankList);

        return "move/feederBankInfo.jsp";
    }

    @RequestMapping("movedCapBanks")
    public String movedCapBanks(ModelMap model, YukonUserContext context) {

        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(context.getYukonUser());

        List<Area> areas = filterCapControlCache.getAreas();
        List<MovedBank> movedBanks = Lists.newArrayList();
        for (Area area : areas) {
            List<CapBankDevice> capBanks = filterCapControlCache.getCapBanksByArea(area.getCcId());
            for (CapBankDevice capBank : capBanks) {
                if (capBank.isBankMoved()) {
                    MovedBank movedBank = new MovedBank(capBank);

                    int currentFeederId = capBank.getParentID();
                    int originalFeederId = capBank.getOrigFeederID();

                    Feeder currentFeeder = filterCapControlCache.getFeeder(currentFeederId);
                    Feeder originalFeeder = filterCapControlCache.getFeeder(originalFeederId);

                    movedBank.setCurrentFeederName(currentFeeder.getCcName());
                    movedBank.setOriginalFeederName(originalFeeder.getCcName());

                    SubStation currentSub = filterCapControlCache.getParentSubstation(currentFeederId);
                    SubStation originalSub = filterCapControlCache.getParentSubstation(originalFeederId);

                    movedBank.setCurrentSubstationId(currentSub.getCcId());
                    movedBank.setOriginalSubstationId(originalSub.getCcId());

                    movedBanks.add(movedBank);
                }
            }
        }
        model.addAttribute("movedBanks", movedBanks);

        return "move/movedCapBanks.jsp";
    }
}