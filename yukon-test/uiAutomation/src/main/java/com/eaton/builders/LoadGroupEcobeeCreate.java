package com.eaton.builders;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoadGroupEcobeeCreate {

    public static class Builder {
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withKwCapacity(double kwCapacity) {
            this.kwCapacity = kwCapacity;
            return this;
        }

        public Builder withDisableGroup(boolean disableGroup) {
            this.disableGroup = disableGroup;
            return this;
        }

        public Builder withDisableControl(boolean disableControl) {
            this.disableControl = disableControl;
            return this;
        }

//      { 
//      "LM_GROUP_ECOBEE": 
//      {
//      "name": "test61",
//      "type": ,
//      "kWCapacity": "23",
//      "disableGroup": false,
//      "disableControl": false
//  }
//}        
        public Object build() {
            JSONObject obj = new JSONObject();
            
            obj.put("name", this.name);
            obj.put("type", "LM_GROUP_ECOBEE");
            obj.put("kwCapacity", this.kwCapacity);
            obj.put("disableGroup", this.disableGroup);
            obj.put("disableControl", this.disableControl);

            return obj;
        }
    }
}
