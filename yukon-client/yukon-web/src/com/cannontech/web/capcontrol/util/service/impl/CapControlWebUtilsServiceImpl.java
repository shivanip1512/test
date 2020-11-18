package com.cannontech.web.capcontrol.util.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.util.JsTreeNode;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class CapControlWebUtilsServiceImpl implements CapControlWebUtilsService {

    private static final ImmutableSet<ControlAlgorithm> showToolTipAlgorithms =
        ImmutableSet.of(ControlAlgorithm.PFACTOR_KW_KVAR);

    @Autowired private CapControlCache capControlCache;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    public List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList) {

        List<ViewableSubBus> viewableList = new ArrayList<>();

        for (SubBus subBus : subBusList) {
            ViewableSubBus viewable = new ViewableSubBus();
            viewable.setSubBusInfo(subBus);
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(subBus.getAlgorithm()));

            if (subBus.getAlternateBusId() > 0) {
                try {
                    SubBus linkedSub = capControlCache.getSubBus(subBus.getAlternateBusId());
                    SubStation linkedStation = capControlCache.getSubstation(linkedSub.getParentID());
                    viewable.setAlternateStationId(linkedStation.getCcId());
                    viewable.setAlternateAreaId(linkedStation.getParentID());
                } catch (NotFoundException exception) {
                    // Alternate dual bus is currently an orphan.
                }
            }

            viewableList.add(viewable);
        }

        return viewableList;
    }

    @Override
    public List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders) {

        List<ViewableFeeder> viewableList = new ArrayList<ViewableFeeder>(feeders.size());

        for (Feeder feeder : feeders) {
            ViewableFeeder viewable = new ViewableFeeder();
            viewable.setFeederInfo(feeder);
            ControlAlgorithm algorithm = feeder.getAlgorithm();
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(algorithm));

            viewableList.add(viewable);
        }

        return viewableList;
    }

    @Override
    public List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks) {

        List<ViewableCapBank> viewableList = new ArrayList<ViewableCapBank>(capBanks.size());
        Map<Integer, LiteYukonPAObject> allPaos = dbCache.getAllPaosMap();

        for (CapBankDevice bank : capBanks) {
            LiteYukonPAObject cbc = allPaos.get(bank.getControlDeviceID());
            ViewableCapBank viewable = new ViewableCapBank();
            boolean isTwoWay = paoDefinitionDao.isTagSupported(cbc.getPaoType(), PaoTag.TWO_WAY_DEVICE);

            viewable.setBankInfo(bank);
            viewable.setCbcInfo(cbc);
            viewable.setTwoWayCbc(isTwoWay);

            viewableList.add(viewable);
        }

        return viewableList;
    }

    @Override
    public List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache,
                                                  boolean isSpecialArea) {
        List<ViewableArea> viewableList = new ArrayList<ViewableArea>(areas.size());

        for (StreamableCapObject area : areas) {
            ViewableArea viewableArea = new ViewableArea();

            List<SubStation> subStations = null;
            if (isSpecialArea) {
                subStations = cache.getSubstationsBySpecialArea(area.getCcId());
            } else {
                subStations = cache.getSubstationsByArea(area.getCcId());
            }

            List<SubBus> subBusList = cache.getSubBusesByArea(area.getCcId());
            Collections.sort(subBusList, CapControlUtils.SUB_DISPLAY_COMPARATOR);
            List<ViewableSubBus> viewableSubBusList = createViewableSubBus(subBusList);
            viewableArea.setSubBusList(viewableSubBusList);

            List<Feeder> feederList = cache.getFeedersByArea(area.getCcId());
            List<ViewableFeeder> viewableFeederList = createViewableFeeder(feederList);
            viewableArea.setFeederList(viewableFeederList);

            List<CapBankDevice> capBankList = cache.getCapBanksByArea(area.getCcId());
            List<ViewableCapBank> viewableCapBankList = createViewableCapBank(capBankList);
            viewableArea.setCapBankList(viewableCapBankList);

            viewableArea.setAreaInfo(area);
            viewableArea.setStationCount(subStations.size());
            viewableList.add(viewableArea);
        }

        return viewableList;
    }

    @Override
    public JsTreeNode buildSimpleHierarchy() {
        // system root node - needed for display
        JsTreeNode systemRoot = new JsTreeNode();

        for (Area area : capControlCache.getAreas()) {
            JsTreeNode areaNode = new JsTreeNode();
            areaNode.setAttribute("id", area.getCcId());
            areaNode.setAttribute("text", area.getCcName());
            addSubstations(area, areaNode);
            systemRoot.addChild(areaNode);
        }

        JsTreeNode.setLeaf(systemRoot);

        return systemRoot;
    }

    private void addSubstations(Area area, JsTreeNode areaNode) {
        for (SubStation substation : capControlCache.getSubstationsByArea(area.getCcId())) {
            JsTreeNode subNode = new JsTreeNode();
            subNode.setAttribute("id", substation.getCcId());
            subNode.setAttribute("text", substation.getCcName());
            addSubstationBuses(substation, subNode);
            areaNode.addChild(subNode);
        }
    }

    private void addSubstationBuses(SubStation substation, JsTreeNode subNode) {
        for (SubBus substationBus : capControlCache.getSubBusesBySubStation(substation)) {
            JsTreeNode busNode = new JsTreeNode();
            busNode.setAttribute("id", substationBus.getCcId());
            busNode.setAttribute("text", substationBus.getCcName());
            addFeeders(substationBus, busNode);
            subNode.addChild(busNode);
        }
    }

    private void addFeeders(SubBus subBus, JsTreeNode subBusNode) {
        for (Feeder feeder : capControlCache.getFeedersBySubBus(subBus.getCcId())) {
            JsTreeNode feederNode = new JsTreeNode();
            feederNode.setAttribute("id", feeder.getCcId());
            feederNode.setAttribute("text", feeder.getCcName());
            subBusNode.addChild(feederNode);
        }
    }

}