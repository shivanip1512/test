package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;

public class CommChannelLocalSharedPortCreateBuilder extends CommChannelSharedCreateBuilder {
    public static class LocalSharedPortBuilder extends SharedBuilder {
        protected String physicalPort;

        public static final String TYPE = "LOCAL_SHARED";

        public LocalSharedPortBuilder(Optional<String> name) {
            super(name);
            this.type = TYPE;
        }

        public LocalSharedPortBuilder withPhysicalPort(Optional<String> physicalPort) {
            this.physicalPort = physicalPort.orElse("com" + faker.number().numberBetween(1, 255));
            return this;
        }

        @Override
        public JSONObject build() {
            JSONObject j = super.build();

            j.put("physicalPort", this.physicalPort);

            return j;
        }
    }

    public static LocalSharedPortBuilder buildDefaultCommChannelLocalSharedPort() {
        return new CommChannelLocalSharedPortCreateBuilder.LocalSharedPortBuilder(Optional.empty());
    }
}
