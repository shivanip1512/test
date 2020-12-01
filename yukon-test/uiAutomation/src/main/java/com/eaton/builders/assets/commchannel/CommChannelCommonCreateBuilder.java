package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;

import com.eaton.builders.assets.commchannel.CommChannelTypes.CommChannelType;
import com.eaton.builders.assets.commchannel.CommChannelTypes.ProtocolWrap;
import com.eaton.builders.assets.commchannel.CommChannelTypes.SharedPortType;

public class CommChannelCommonCreateBuilder extends CommChannelCreateBuilder {

    public static class CommonBuilder extends Builder {
        protected Integer carrierDetectWaitMs;
        protected ProtocolWrap protocolWrap;
        protected SharedPortType sharedPortType;
        protected Integer socketNumber;

        public CommonBuilder(Optional<String> name, CommChannelType commType) {
            super(name, commType);
        }

        public CommonBuilder withCarrierDetectWaitMs(Optional<Integer> carrierDetectWaitMs) {
            this.carrierDetectWaitMs = carrierDetectWaitMs.orElse(faker.number().numberBetween(0, 999));
            return this;
        }

        public CommonBuilder withProtocolWrap(Optional<CommChannelTypes.ProtocolWrap> protocolWrap) {
            this.protocolWrap = protocolWrap.orElse(CommChannelTypes.ProtocolWrap.IDLC);

            return this;
        }

        public CommonBuilder withSharedPortType(Optional<CommChannelTypes.SharedPortType> sharedPortType) {
            this.sharedPortType = sharedPortType.orElse(CommChannelTypes.SharedPortType.getRandomBaudRate());
            
            return this;
        }

        public CommonBuilder withSocketNumber(Optional<Integer> socketNumber) {
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
            s.put("sharedPortType", this.sharedPortType);
            s.put("sharedSocketNumber", this.socketNumber);
            j.put("sharing", s);

            return j;
        }
    }
}
