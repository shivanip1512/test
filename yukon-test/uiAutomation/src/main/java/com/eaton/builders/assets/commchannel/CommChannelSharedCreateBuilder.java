package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;
import com.eaton.builders.assets.commchannel.CommChannelTypes.ProtocolWrap;
import com.eaton.builders.assets.commchannel.CommChannelTypes.SharedPortType;

public class CommChannelSharedCreateBuilder extends CommChannelCreateBuilder {

    public static class SharedBuilder extends Builder {
        private Integer carrierDetectWaitMs;
        private ProtocolWrap protocolWrap;
        private SharedPortType sharedPortType;
        private Integer socketNumber;

        public SharedBuilder(Optional<String> name) {
            super(name);
        }

        public SharedBuilder withCarrierDetectWaitMs(Optional<Integer> carrierDetectWaitMs) {
            this.carrierDetectWaitMs = carrierDetectWaitMs.orElse(faker.number().numberBetween(0, 999));
            return this;
        }

        public SharedBuilder withProtocolWrap(Optional<CommChannelTypes.ProtocolWrap> protocolWrap) {
            this.protocolWrap = protocolWrap.orElse(CommChannelTypes.ProtocolWrap.IDLC);

            return this;
        }

        public SharedBuilder withSharedPortType(Optional<CommChannelTypes.SharedPortType> sharedPortType) {
            this.sharedPortType = sharedPortType.orElse(CommChannelTypes.SharedPortType.getRandomBaudRate());
            
            return this;
        }

        public SharedBuilder withSocketNumber(Optional<Integer> socketNumber) {
            if (!this.sharedPortType.getSharedPortType().equals(CommChannelTypes.SharedPortType.NONE.getSharedPortType())) {
                this.socketNumber = socketNumber.orElse(faker.number().numberBetween(0, 65535));
            }

            return this;
        }

        @Override
        public JSONObject build() {
            JSONObject j = super.build();

            j.put("carrierDetectWaitInMilliseconds", this.carrierDetectWaitMs);
            j.put("protocolWrap", this.protocolWrap);

            JSONObject s = new JSONObject();
            s.put("sharedPortType", this.sharedPortType.getSharedPortType());
            s.put("sharedSocketNumber", this.socketNumber);
            j.put("sharing", s);

            return j;
        }
    }
}
