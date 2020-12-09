package com.eaton.builders.assets.commchannel;

import java.util.Optional;

import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;

public class CommChannelTcpPortCreateBuilder extends CommChannelCreateBuilder {
    public static class TcpPortBuilder extends Builder {
        
        public TcpPortBuilder(Optional<String> name, CommChannelType commType) {
            super(name, commType);
        }      
    }
}
