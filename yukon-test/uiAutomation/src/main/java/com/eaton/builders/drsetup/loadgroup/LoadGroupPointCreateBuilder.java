package com.eaton.builders.drsetup.loadgroup;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.framework.TestDbDataType;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupPointCreateBuilder {

    public static class Builder {
        private Faker faker = new Faker();

        private static final String TYPE = "LM_GROUP_POINT";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private int deviceUsageId;
        private int pointUsageId;
        private int startControlRawState;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withName(String name) {
            this.name = name;
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
            this.disableControl = disableControl.orElse(true);
            return this;
        }

        public Builder withPointStartControlRawState(
                Optional<LoadGroupEnums.PointStartControlRawState> PointStartControlRawState) {
            LoadGroupEnums.PointStartControlRawState randomPointStartControlRawState = PointStartControlRawState
                    .orElse(LoadGroupEnums.PointStartControlRawState.getRandomPointStartControlRawState());
            this.startControlRawState = randomPointStartControlRawState.getPointStartControlRawState();
            return this;
        }

        public Builder withDeviceUsageId(Optional<Integer> deviceUsageId) {
            Integer id = TestDbDataType.VoltVarData.CBC_EDIT_ID.getId();
            this.deviceUsageId = deviceUsageId.orElse(id);
            return this;
        }

        public Builder withPointUsageId(Optional<TestDbDataType.PointData> pointUsageId) {
            TestDbDataType.PointData randomPointUsageId = pointUsageId.orElse(TestDbDataType.PointData.getRandomPointId());
            this.pointUsageId = randomPointUsageId.getId();
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
            JSONObject jo1 = new JSONObject();
            jo1.put("rawState", this.startControlRawState);
            jo.put("startControlRawState", jo1);
            JSONObject jo2 = new JSONObject();
            jo2.put("id", this.deviceUsageId);
            jo.put("deviceUsage", jo2);
            JSONObject jo3 = new JSONObject();
            jo3.put("id", this.pointUsageId);
            jo.put("pointUsage", jo3);
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

    public static Builder buildDefaultPointLoadGroup() {
        return new LoadGroupPointCreateBuilder.Builder(Optional.empty())
                .withDisableControl(Optional.of(false))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withPointStartControlRawState(Optional.empty())
                .withPointUsageId(Optional.empty())
                .withDeviceUsageId(Optional.empty());
    }
}