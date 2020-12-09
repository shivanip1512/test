package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;
import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;

public class CommChannelUdpTerminalServerCreateBuilder extends CommChannelCommonCreateBuilder {

    public static class UdpTerminalServerBuilder extends CommonBuilder {
        private Integer portNumber;
        private String encryptionKey;

        public UdpTerminalServerBuilder(Optional<String> name, CommChannelType commType) {
            super(name, commType);
        }

        public UdpTerminalServerBuilder withPortNumber(Optional<Integer> portNumber) {
            this.portNumber = portNumber.orElse(faker.number().numberBetween(1, 65534));
            return this;
        }

        public UdpTerminalServerBuilder withEncryptionKey(Optional<String> encryptionKey) {
        	//The Encryption Key must be 16 bytes long (i.e. 32 hex values). It is not allowed to be shorter. 
            this.encryptionKey = encryptionKey.orElse(faker.random().hex(32));
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
}
