package com.eaton.rest.api.assets.utils;

import org.json.simple.JSONObject;

import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.assets.AssetsDeleteRequestAPI;
import com.eaton.rest.api.assets.AssetsUpdateRequestAPI;
import io.restassured.response.ExtractableResponse;
import com.eaton.rest.api.dr.JsonFileHelper;

public class CommChannelUtils {
        
        public ExtractableResponse<?> createCommChannel(String payload) {
                Object createBody = JsonFileHelper.parseJSONFile(payload);
                return AssetsCreateRequestAPI.createCommChannelTCP(createBody);
        }
        
        @SuppressWarnings("unchecked")
        public void updateCommChannel(String payload, String portId) { 
                Object updateBody = JsonFileHelper.parseJSONFile(payload);
                JSONObject jsonObj = (JSONObject) updateBody;
                //Update value for key 'enable'
                jsonObj.put("enable", false);
                AssetsUpdateRequestAPI.updateCommChannelTCP(jsonObj, portId);
        }
        
        public void deleteCommChannel(String portId) {
                AssetsDeleteRequestAPI.deleteCommChannelTCP(portId);
        }
}
