package com.eaton.builders.assets.virtualdevices;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.virtualdevice.VirtualDeviceRequest;

import io.restassured.response.ExtractableResponse;

public class VirtualDeviceCreateBuilder {
    public static class Builder {
        private String name;
        private String type;
        private boolean enable;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT Virtual Device " + uuid);
            this.type = "VIRTUAL_SYSTEM";
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }        

        public Builder withEnable(Optional<Boolean> enable) {
            this.enable = enable.orElse(true);
            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("name", this.name);
            j.put("type", this.type);
            j.put("enable", this.enable);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = VirtualDeviceRequest.createVirtualDevice(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }
}
