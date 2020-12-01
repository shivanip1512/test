package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;

import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;
import com.eaton.builders.assets.commchannel.CommChannelTypes.PhysicalPortType;

public class CommChannelLocalSharedPortCreateBuilder extends CommChannelCommonCreateBuilder {
    public static class LocalSharedPortBuilder extends CommonBuilder {
        protected PhysicalPortType physicalPort;

        public LocalSharedPortBuilder(Optional<String> name, CommChannelType commType) {
            super(name, commType);
        }

        public LocalSharedPortBuilder withPhysicalPort(Optional<PhysicalPortType> physicalPort) {
            this.physicalPort = physicalPort.orElse(PhysicalPortType.getRandomPhysicalPort());
            
            return this;
        }

        @Override
        public JSONObject build() {
            JSONObject j = super.build();

            j.put("physicalPort", this.physicalPort);

            return j;
        }
    }
}
