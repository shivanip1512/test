package com.cannontech.dr.pxmw.helper;

import java.util.Arrays;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Sets;

public class PxMWChannelIDToAttributeHelper {

    
    public static enum ChannelIDAttributes {
        // Channel 12343?
        LCR_6200C(PaoType.LCR6200C,
                MWChannel.S_RELAY_STATUS,
                MWChannel.LOAD_STATE,
                MWChannel.LUF_RESTORE,
                MWChannel.LUF_TRIGGER,
                MWChannel.LUV_RESTORE,
                MWChannel.LUV_TRIGGER
                ),
        LCR_6600C(PaoType.LCR6600C,
                MWChannel.S_RELAY_STATUS,
                MWChannel.LOAD_STATE,
                MWChannel.XCOM,
                MWChannel.LUF_RESTORE,
                MWChannel.LUF_TRIGGER
                ),
        LCR_DISCONNECT_C(PaoType.LCR_DISCONNECT_C,
                MWChannel.S_RELAY_STATUS,
                MWChannel.LOAD_STATE
                )
        ;
        
        private PaoType paoType;
        private Set<MWChannel> supportedMWChannels;
        private ChannelIDAttributes(PaoType paoType, MWChannel... supportedMWChannels) {
            this.paoType = paoType;
            this.supportedMWChannels = Sets.newEnumSet(Arrays.asList(supportedMWChannels), MWChannel.class);
        }
        
        PaoType getPaoType() {
            return paoType;
        }
        
        Set<MWChannel> getSupportedMWChannels() {
            return supportedMWChannels;
        }
    }

}