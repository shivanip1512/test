package com.eaton.builders;

import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

import com.github.javafaker.Faker;

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
       
        public Object build() {
            JSONObject obj = new JSONObject();
            JSONObject params = new JSONObject();
            params.put("name", this.name);
            params.put("type", TYPE);
            params.put("kwCapacity", this.kwCapacity);
            params.put("disableGroup", this.disableGroup);
            params.put("disableControl", this.disableControl);

            obj.put(TYPE, params);

            return obj;
        }
    }
}
