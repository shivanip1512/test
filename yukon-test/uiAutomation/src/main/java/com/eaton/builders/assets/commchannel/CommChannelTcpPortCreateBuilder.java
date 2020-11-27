package com.eaton.builders.assets.commchannel;

import java.util.Optional;

public class CommChannelTcpPortCreateBuilder extends CommChannelCreateBuilder {
    public static class TcpPortBuilder extends Builder {
        
        public static final String TYPE = "TCPPORT";
        public static final String DEFAULT_NAME = "TCP Port";

        public TcpPortBuilder(Optional<String> name) {
            super(name);
            this.type = TYPE;
            this.defaultName = DEFAULT_NAME;
            withName(name);
        }      
    }

    public static TcpPortBuilder buildDefaultCommChannelTcpPort() {
        return new CommChannelTcpPortCreateBuilder.TcpPortBuilder(Optional.empty());
    }
}
