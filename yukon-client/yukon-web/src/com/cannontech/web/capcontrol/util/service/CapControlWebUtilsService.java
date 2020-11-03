package com.cannontech.web.capcontrol.util.service;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.util.JsTreeNode;

public interface CapControlWebUtilsService {

    List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList);

    List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders);

    List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks);

    List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea);

    JsTreeNode buildSimpleHierarchy();

}