package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.commchannel.CommChannelRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public abstract class CommChannelCreateBuilder {
    public abstract static class Builder {
    	protected Faker faker = new Faker();
    	protected String type;
    	protected String name;
    	protected String defaultName;
    	protected boolean enable;
    	protected String baudRate;
    	protected CommChannelTiming timing;

        public Builder(Optional<String> name) {
        	this.enable = true;
            this.baudRate = CommChannelTypes.BaudRate.BAUD_1200.getBaudRate();
            this.timing = new CommChannelTiming();
        }
        
        public Builder withName(Optional<String> name) {
        	String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT " + defaultName + " " + uuid);
            return this;
        }

        public Builder withEnable(Optional<Boolean> enable) {
            this.enable = enable.orElse(true);
            return this;
        }
        
        public Builder withBaudRate(Optional<CommChannelTypes.BaudRate> baudRate) {
        	CommChannelTypes.BaudRate rate = baudRate.orElse(CommChannelTypes.BaudRate.BAUD_300);
        	
        	this.baudRate = rate.getBaudRate();
            return this;
        }
        
        public Builder withTiming(Optional<CommChannelTiming> timing) {
        	if(timing.isPresent()) {
        		this.timing = timing.get();
                return this;
        	} else {
        		return this.withPreTxWait(Optional.empty()).withRtsToTxWait(Optional.empty()).withPostTxWait(Optional.empty()).withReceiveDataWait(Optional.empty()).withExtraTimeOut(Optional.empty());
        	}
        }
        
        public Builder withPreTxWait(Optional<Integer> preTxWait) {
            this.timing.setPreTxWait(preTxWait.orElse(faker.number().numberBetween(0, 10000000)));
            return this;
        }
        
        public Builder withRtsToTxWait(Optional<Integer> rtsToTxWait) {
            this.timing.setRtsToTxWait(rtsToTxWait.orElse(faker.number().numberBetween(0, 10000000)));
            return this;
        }
        
        public Builder withPostTxWait(Optional<Integer> postTxWait) {
            this.timing.setPostTxWait(postTxWait.orElse(faker.number().numberBetween(0, 10000000)));
            return this;
        }

        public Builder withReceiveDataWait(Optional<Integer> receiveDataWait) {
            this.timing.setReceiveDataWait(receiveDataWait.orElse(faker.number().numberBetween(0, 1000)));
            return this;
        }
        
        public Builder withExtraTimeOut(Optional<Integer> extraTimeOut) {
            this.timing.setExtraTimeOut(extraTimeOut.orElse(faker.number().numberBetween(0, 999)));
            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("type", this.type);
            j.put("name", this.name);
            j.put("enable", this.enable);
            j.put("baudRate", this.baudRate);


            JSONObject t = new JSONObject();
            t.put("preTxWait", this.timing.getPreTxWait());
            t.put("rtsToTxWait", this.timing.getRtsToTxWait());
            t.put("postTxWait", this.timing.getPostTxWait());
            t.put("receiveDataWait", this.timing.getReceiveDataWait());
            t.put("extraTimeOut", this.timing.getExtraTimeOut());
            j.put("timing", t);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = CommChannelRequest.createCommChannel(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
        
        public String getType() {
        	return type;
        }
        
        public String getName() {
        	return name;
        }
        
        public Boolean getEnable() {
        	return enable;
        }
        
        public String getBaudRate() {
        	return baudRate;
        }
        
        public CommChannelTiming getTiming() {
        	return timing;
        }
    }
}
