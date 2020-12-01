package com.eaton.builders.assets.commchannel;

import java.util.Optional;

public class CommChannelTcpPortCreateBuilder extends CommChannelCreateBuilder {
    public static class TcpPortBuilder extends Builder {
        
        public TcpPortBuilder(Optional<String> name) {
            super(name);
            this.type = "TCPPORT";
        }      
    }

    public static TcpPortBuilder buildDefaultCommChannelTcpPort() {
        return new CommChannelTcpPortCreateBuilder.TcpPortBuilder(Optional.empty());
    }
}
