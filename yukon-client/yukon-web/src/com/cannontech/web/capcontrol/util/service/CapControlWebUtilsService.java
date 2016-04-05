package com.cannontech.web.capcontrol.util.service;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.capcontrol.models.NavigableArea;
import com.cannontech.web.capcontrol.models.NavigableCapBank;
import com.cannontech.web.capcontrol.models.NavigableFeeder;
import com.cannontech.web.capcontrol.models.NavigableSubstation;
import com.cannontech.web.capcontrol.models.NavigableSubstationBus;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;

public interface CapControlWebUtilsService {

    List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList);

    List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders);

    List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks);

    List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea);

    List<NavigableArea> buildSimpleHierarchy();

    List<NavigableSubstation> getSimpleSubstations(Area area);

    List<NavigableSubstationBus> getSimpleSubstationBuses(SubStation substation);

    List<NavigableFeeder> getSimpleFeeders(SubBus subBus);

    List<NavigableCapBank> getSimpleCapBanks(Feeder feeder);

}