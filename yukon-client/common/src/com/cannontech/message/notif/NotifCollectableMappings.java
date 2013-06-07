package com.cannontech.message.notif;

import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class NotifCollectableMappings {

    private static final DefineCollectable[] DEFAULT_MAPPINGS = {
        CollectableMappings.OrderedVector,
        new DefColl_NotifAlarmMsg(), 
        new DefColl_NotifLMControlMsg(), 
        new DefColl_VoiceDataRequestMsg(),
        new DefColl_VoiceDataResponseMsg(),
        new DefColl_NotifCompletedMsg(),
        new DefColl_NotifEmailMsg(),
        new DefColl_CurtailmentEventMsg(),
        new DefColl_CurtailmentEventDeleteMsg(),
        new DefColl_EconomicEventMsg(),
        new DefColl_EconomicEventDeleteMsg(),
        new DefColl_ProgramActionMsg() 
        };

    public static DefineCollectable[] getMappings() {
        return DEFAULT_MAPPINGS;
    }
}
