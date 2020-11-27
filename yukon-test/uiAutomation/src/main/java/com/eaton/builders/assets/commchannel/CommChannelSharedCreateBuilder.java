package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import org.json.JSONObject;
import com.eaton.builders.assets.commchannel.CommChannelTypes.ProtocolWrap;

public class CommChannelSharedCreateBuilder extends CommChannelCreateBuilder {

    public static class SharedBuilder extends Builder {  
        private Integer carrierDetectWaitMs;
        protected CommChannelSharedPort sharedPort;
        private ProtocolWrap protocolWrap;
        
        public SharedBuilder(Optional<String> name) {
        	super(name);
        	sharedPort = new CommChannelSharedPort();
        	carrierDetectWaitMs = 0;
        	protocolWrap = ProtocolWrap.IDLC;
        }
        
        public SharedBuilder withCarrierDetectWaitMs(Optional<Integer> carrierDetectWaitMs) {
            this.carrierDetectWaitMs = carrierDetectWaitMs.orElse(faker.number().numberBetween(0, 999));
            return this;
        }
        public SharedBuilder withProtocolWrap(Optional<CommChannelTypes.ProtocolWrap> protocolWrap) {
        	this.protocolWrap = protocolWrap.orElse(CommChannelTypes.ProtocolWrap.IDLC);
            
            return this;
        }
        
        public SharedBuilder withSharedPort(Optional<CommChannelSharedPort> sharedPort) {
        	if(sharedPort.isPresent()) {
        		this.sharedPort = sharedPort.get();
                return this;
        	} else {
        		return this.withSharedPortType(Optional.empty()).withSharedSocketNumber(Optional.empty());
        	}
        }
        
        public SharedBuilder withSharedPortType(Optional<CommChannelTypes.SharedPortType> sharedPortType) {
        	sharedPort.setSharedPortType(sharedPortType.orElse(CommChannelTypes.SharedPortType.NONE));
            return this;
        }
        
        public SharedBuilder withSharedSocketNumber(Optional<Integer> sharedSocketNumber) {
            if (this.sharedPort.getSharedPortType() != CommChannelTypes.SharedPortType.NONE) {
                this.sharedPort.setSharedSocketNumber(sharedSocketNumber.orElse(faker.number().numberBetween(0, 65535)));
            }
            
            return this;
        }
        
        public JSONObject build() {                  
        	JSONObject j = super.build();
        	     	
        	j.put("carrierDetectWaitInMilliseconds", this.carrierDetectWaitMs);
        	j.put("protocolWrap", this.protocolWrap);
        	
        	JSONObject s = new JSONObject();
            s.put("sharedPortType", this.sharedPort.getSharedPortType());
            s.put("sharedSocketNumber", this.sharedPort.getSharedSocketNumber());
            j.put("sharing", s);
            
            return j;
        } 
    }
}
