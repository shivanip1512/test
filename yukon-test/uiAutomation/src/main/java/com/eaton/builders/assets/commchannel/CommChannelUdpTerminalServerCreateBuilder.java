package com.eaton.builders.assets.commchannel;

import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
}
