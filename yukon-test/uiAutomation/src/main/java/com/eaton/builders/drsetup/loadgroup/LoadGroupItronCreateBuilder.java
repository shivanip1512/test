package com.eaton.builders.drsetup.loadgroup;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupItronCreateBuilder {
    public static class Builder {
        private Faker faker = new Faker();

        private static final String TYPE = "LM_GROUP_ITRON";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private int virtualRelayId;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withRelay(Optional<Integer> virtualRelayId) {
            this.virtualRelayId = virtualRelayId.orElse(faker.number().numberBetween(1, 8));

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
            jo.put("virtualRelayId", this.virtualRelayId);

            j.put(TYPE, jo);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();

            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadGroup(request.toString());

            String id = createResponse.path("groupId").toString();

            ExtractableResponse<?> er = DrSetupGetRequest.getLoadGroup(Integer.parseInt(id));

            String res = er.asString();
            JSONObject response = new JSONObject(res);
            JSONObject jsonResponse = response.getJSONObject(TYPE);

            return new Pair<>(request, jsonResponse);
        }
    }
}
