package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class CommChannelCreateBuilder {
    public static class Builder {
        protected Faker faker = new Faker();
        protected String type;
        protected String name;
        protected String defaultName;
        protected boolean enable;
        protected String baudRate;
        protected Integer preTxWait;
        protected Integer rtsToTxWait;
        protected Integer postTxWait;
        protected Integer receiveDataWait;
        protected Integer extraTimeOut;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT " + defaultName + " " + uuid);
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

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("type", this.type);
            j.put("name", this.name);
            j.put("enable", this.enable);
            j.put("baudRate", this.baudRate);

            JSONObject t = new JSONObject();
            t.put("preTxWait", this.preTxWait);
            t.put("rtsToTxWait", this.rtsToTxWait);
            t.put("postTxWait", this.postTxWait);
            t.put("receiveDataWait", this.receiveDataWait);
            t.put("extraTimeOut", this.extraTimeOut);
            j.put("timing", t);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }
}
