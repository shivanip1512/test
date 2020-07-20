package com.eaton.builders;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupEcobeeCreate {

    public static class Builder {
        private Faker faker = new Faker();
        
        private static final String TYPE = "LM_GROUP_ECOBEE";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;        

        public Builder withName(Optional<String> name) {
            String u = UUID.randomUUID().toString();            
            String uuid = u.replace("-", "");
            
            this.name = name.orElse("AT LG " + uuid);
            return this;
        }

        public Builder withKwCapacity(Optional<Double> kwCapacity) {
            this.kwCapacity = kwCapacity.orElse(faker.number().randomDouble(3, 0, 99999));            
            return this;
        }

        public Builder withDisableGroup(Optional<Boolean> disableGroup) {
            this.disableGroup = disableGroup.orElse(false);
            return this;
        }

        public Builder withDisableControl(Optional<Boolean> disableControl) {
            this.disableControl = disableControl.orElse(false);
            return this;
        }
       
        public JSONObject build() {            
            JSONObject j = new JSONObject();            
            
            JSONObject jo = new JSONObject();            
            jo.put("name", this.name);
            jo.put("type", TYPE);
            jo.put("kWCapacity", this.kwCapacity);
            jo.put("disableGroup", this.disableGroup);
            jo.put("disableControl", this.disableControl);            

            j.put(TYPE, jo);
            
            return j;
        }
        
        public Pair<JSONObject, ExtractableResponse<?>> create() {
            JSONObject json = build(); 
            
            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadGroup(json.toString());
            
            return new Pair<>(json, createResponse);
        }
    }
}
