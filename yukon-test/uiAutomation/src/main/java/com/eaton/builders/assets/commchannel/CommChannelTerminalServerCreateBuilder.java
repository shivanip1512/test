package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;

public class CommChannelTerminalServerCreateBuilder extends CommChannelSharedCreateBuilder {
    public static class TerminalServerBuilder extends SharedBuilder {
    	protected Integer portNumber;
    	protected String ipAddress;
    	
        public static final String TYPE = "TSERVER_SHARED";
        public static final String DEFAULT_NAME = "TCP Terminal Server";

        public TerminalServerBuilder(Optional<String> name) {
            super(name);
            this.type = TYPE;
            this.defaultName = DEFAULT_NAME;
            portNumber = 10000;
            ipAddress = "127.0.0.1";
            withName(name);
        }
        
        public TerminalServerBuilder withPortNumber(Optional<Integer> portNumber) {
            this.portNumber = portNumber.orElse(faker.number().numberBetween(1, 65534));
            return this;
        }
        
        public TerminalServerBuilder withIPAddress(Optional<String> ipAddress) {
            this.ipAddress = ipAddress.orElse(faker.internet().ipV4Address());
            return this;
        }
        
        public JSONObject build() {
        	JSONObject j = super.build();
        	
        	j.put("portNumber", this.portNumber);
        	j.put("ipAddress", this.ipAddress);
        	     
            return j;
        }
    }

    public static TerminalServerBuilder buildDefaultCommChannelTerminalServer() {
        return new CommChannelTerminalServerCreateBuilder.TerminalServerBuilder(Optional.empty());
    }
}
