package com.eaton.builders.drsetup.loadgroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.javatuples.Pair;
import org.json.JSONObject;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.AddressLevelMCT;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RelayUsage;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupMCTCreateBuilder {

    public static class Builder {
        private Faker faker = new Faker();
        private static final String TYPE = "LM_GROUP_MCT";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private int routeId;
        private int address;
        private int mctDeviceId;
        private AddressLevelMCT addressLevelMCT;
        private List<RelayUsage> relayUsage;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withRelayUsage(List<RelayUsage> relayUsage) {
            this.relayUsage = relayUsage;
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

        public Builder withAddress(int address) {
            this.address = address;
            return this;
        }

        public Builder withlevel(AddressLevelMCT addressLevelMCT) {
            this.addressLevelMCT = addressLevelMCT;
            return this;
        }

        public Builder withCommunicationRoute(int routeId) {
            this.routeId = routeId;
            return this;
        }

        public Builder withMctDeviceId(int mctDeviceId) {
            this.mctDeviceId = mctDeviceId;
            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            JSONObject jo = new JSONObject();

            jo.put("name", this.name);
            jo.put("type", TYPE);
            jo.put("routeId", this.routeId);
            jo.put("level", this.addressLevelMCT);
            jo.put("address", this.address);
            jo.put("kWCapacity", this.kwCapacity);
            jo.put("disableGroup", this.disableGroup);
            jo.put("disableControl", this.disableControl);
            jo.put("mctDeviceId", this.mctDeviceId);
            jo.put("relayUsage", this.relayUsage);
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