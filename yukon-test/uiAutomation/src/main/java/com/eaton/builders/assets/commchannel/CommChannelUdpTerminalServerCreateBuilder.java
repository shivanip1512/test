package com.eaton.builders.assets.commchannel;

import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;


public class CommChannelUdpTerminalServerCreateBuilder extends CommChannelSharedCreateBuilder {

    public static class UdpTerminalServerBuilder extends SharedBuilder {  
        private Integer portNumber;
        private String encryptionKey;
        
        public UdpTerminalServerBuilder(Optional<String> name) {
        	super(name);
        	this.type = "UDPPORT";
        }
        
        public UdpTerminalServerBuilder withPortNumber(Optional<Integer> portNumber) {
            this.portNumber = portNumber.orElse(faker.number().numberBetween(1, 65534));
            return this;
        }
        
        public UdpTerminalServerBuilder withEncryptionKey(Optional<String> encryptionKey) {
            Random random = ThreadLocalRandom.current();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String encoded = Base64.getUrlEncoder().encodeToString(randomBytes);
            
            this.encryptionKey = encryptionKey.orElse(encoded);
            return this;
        }
        
        @Override
        public JSONObject build() {                  
        	JSONObject j = super.build();
        	
        	j.put("portNumber", this.portNumber);
            j.put("keyInHex", this.encryptionKey);
            
            return j;
        } 
    }
    
    public static UdpTerminalServerBuilder buildDefaultCommChannelUdpTerminalServer() {
        return new CommChannelUdpTerminalServerCreateBuilder.UdpTerminalServerBuilder(Optional.empty());
    }
}
