package com.eaton.builders.assets.commchannel;

import java.util.Optional;
import java.util.UUID;

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
        private Integer carrierDetectWait;
        private String protocolWrap;
        private Integer preTxWait;
        private Integer rtsToTxWait;
        private Integer postTxWait;
        private Integer receiveDataWait;
        private Integer extraTimeOut;
        private String sharedPortType;
        private String sharedSocketNumber;
        
        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();            
            String uuid = u.replace("-", "");
            
            this.name = name.orElse("AT Udp " + uuid);
        }

        public Builder withEnable(Optional<Boolean> enable) {
            this.enable = enable.orElse(true);
            return this;
        }
       
        public JSONObject build() {      
            String u = UUID.randomUUID().toString();            
            String uuid = u.replace("-", "");
            
            JSONObject jo = new JSONObject();            
            jo.put("name", this.name);
            jo.put("enable", this.enable);

            JSONObject t = new JSONObject();
            t.put("", "");
            jo.put("timing", t);
            
            JSONObject s = new JSONObject();
            s.put("", "");
            jo.put("sharing", s);

            jo.put("keyInHex", uuid);
            return jo;
        }
        
//        public Pair<JSONObject, JSONObject> create() {
//            JSONObject request = build(); 
//            
//            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadGroup(request.toString());
//            
//            String id = createResponse.path("groupId").toString();
//            
//            ExtractableResponse<?> er = DrSetupGetRequest.getLoadGroup(Integer.parseInt(id)); 
//            
//            String res = er.asString();
//            JSONObject response = new JSONObject(res);
//            JSONObject jsonResponse = response.getJSONObject();
//            
//            return new Pair<>(request, jsonResponse);
//        }
    }
}
