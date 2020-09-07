package com.eaton.builders.assets.commchannel;

import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CommChannelUdpCreateBuilder {

    public static class Builder {
        private Faker faker = new Faker();
        
        private String name;
        private boolean enable;
        private String baudRate;
        private Integer portNumber;
        private Integer carrierDetectWaitMs;
        private String encryptionKey;
        private String protocolWrap;
        private Integer preTxWait;
        private Integer rtsToTxWait;
        private Integer postTxWait;
        private Integer receiveDataWait;
        private Integer extraTimeOut;
        private String sharedPortType;
        private Integer sharedSocketNumber;
        
        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();            
            String uuid = u.replace("-", "");
            
            this.name = name.orElse("AT Udp " + uuid);
        }

        public Builder withEnable(Optional<Boolean> enable) {
            this.enable = enable.orElse(true);
            return this;
        }
        
        public Builder withBaudRate(Optional<CommChannelTypes.BaudRate> baudRate) {
            CommChannelTypes.BaudRate rate = baudRate.orElse(CommChannelTypes.BaudRate.BAUD_1200);
            
            this.baudRate = rate.getBaudRate();
            return this;
        }
        
        public Builder withPortNumber(Optional<Integer> portNumber) {
            this.portNumber = portNumber.orElse(faker.number().numberBetween(1, 65534));
            return this;
        }
        
        public Builder withCarrierDetectWaitMs(Optional<Integer> carrierDetectWaitMs) {
            this.carrierDetectWaitMs = carrierDetectWaitMs.orElse(faker.number().numberBetween(0, 999));
            return this;
        }
        
        public Builder withEncryptionKey(Optional<String> encryptionKey) {
            Random random = ThreadLocalRandom.current();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String encoded = Base64.getUrlEncoder().encodeToString(randomBytes);
            
            this.encryptionKey = encryptionKey.orElse(encoded);
            return this;
        }
        
        public Builder withProtocolWrap(Optional<CommChannelTypes.ProtocolWrap> protocolWrap) {
            CommChannelTypes.ProtocolWrap wrap = protocolWrap.orElse(CommChannelTypes.ProtocolWrap.IDLC);
            
            this.protocolWrap = wrap.getProtocolWrap();
            return this;
        }
        
        public Builder withPreTxWait(Optional<Integer> preTxWait) {
            this.preTxWait = preTxWait.orElse(faker.number().numberBetween(0, 10000000));
            return this;
        }
        
        public Builder withRtsToTxWait(Optional<Integer> rtsToTxWait) {
            this.rtsToTxWait = rtsToTxWait.orElse(faker.number().numberBetween(0, 10000000));
            return this;
        }
        
        public Builder withPostTxWait(Optional<Integer> postTxWait) {
            this.postTxWait = postTxWait.orElse(faker.number().numberBetween(0, 10000000));
            return this;
        }

        public Builder withReceiveDataWait(Optional<Integer> receiveDataWait) {
            this.receiveDataWait = receiveDataWait.orElse(faker.number().numberBetween(0, 1000));
            return this;
        }
        
        public Builder withExtraTimeOut(Optional<Integer> extraTimeOut) {
            this.extraTimeOut = extraTimeOut.orElse(faker.number().numberBetween(0, 999));
            return this;
        }
        
        public Builder withSharedPortType(Optional<CommChannelTypes.SharedPortType> sharedPortType) {
            CommChannelTypes.SharedPortType type = sharedPortType.orElse(CommChannelTypes.SharedPortType.NONE);
            
            this.sharedPortType = type.getSharedPortType();
            return this;
        }
        
        public Builder withSharedSocketNumber(Optional<Integer> sharedSocketNumber) {
            if (this.sharedPortType.equalsIgnoreCase(CommChannelTypes.SharedPortType.ACS.getSharedPortType())|| this.sharedPortType.equalsIgnoreCase(CommChannelTypes.SharedPortType.ILEX.getSharedPortType())) {
                this.sharedSocketNumber = sharedSocketNumber.orElse(faker.number().numberBetween(0, 65535));
            }
            
            return this;
        }
        
        public JSONObject build() {                  
            JSONObject jo = new JSONObject();            
            jo.put("name", this.name);
            jo.put("enable", this.enable);
            jo.put("baudRate", this.baudRate);
            jo.put("type", "UDPPORT");
            jo.put("portNumber", this.portNumber);
            jo.put("carrierDetectWaitInMilliseconds", this.carrierDetectWaitMs);
            jo.put("protocolWrap", this.protocolWrap);

            JSONObject t = new JSONObject();
            t.put("preTxWait", this.preTxWait);
            t.put("rtsToTxWait", this.rtsToTxWait);
            t.put("postTxWait", this.postTxWait);
            t.put("receiveDataWait", this.receiveDataWait);
            t.put("extraTimeOut", this.extraTimeOut);
            jo.put("timing", t);
            
            JSONObject s = new JSONObject();
            s.put("sharedPortType", this.sharedPortType);
            s.put("sharedSocketNumber", this.sharedSocketNumber);
            jo.put("sharing", s);

            jo.put("keyInHex", this.encryptionKey);
            return jo;
        }
        
        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build(); 
            
            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadGroup(request.toString());
            
            String id = createResponse.path("groupId").toString();
            
            ExtractableResponse<?> er = DrSetupGetRequest.getLoadGroup(Integer.parseInt(id)); 
            
            String res = er.asString();
            JSONObject response = new JSONObject(res);
            
            return new Pair<>(request, response);
        }
    }
}
