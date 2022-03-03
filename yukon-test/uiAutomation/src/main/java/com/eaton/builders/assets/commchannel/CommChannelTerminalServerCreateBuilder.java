package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;

import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;

public class CommChannelTerminalServerCreateBuilder extends CommChannelCommonCreateBuilder {
    public static class TerminalServerBuilder extends CommonBuilder {
        protected Integer portNumber;
        protected String ipAddress;

        public TerminalServerBuilder(Optional<String> name, CommChannelType commType) {
            super(name, commType);
        }

        public TerminalServerBuilder withPortNumber(Optional<Integer> portNumber) {
            this.portNumber = portNumber.orElse(faker.number().numberBetween(1, 65534));

            return this;
        }

        public TerminalServerBuilder withIPAddress(Optional<String> ipAddress) {
            this.ipAddress = ipAddress.orElse(faker.internet().ipV4Address());

            return this;
        }

        @Override
        public JSONObject build() {
            JSONObject j = super.build();

            j.put("portNumber", this.portNumber);
            j.put("ipAddress", this.ipAddress);

            return j;
        }
    }
}
