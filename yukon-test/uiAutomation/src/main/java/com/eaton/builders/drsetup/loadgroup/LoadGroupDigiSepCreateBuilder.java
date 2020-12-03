package com.eaton.builders.drsetup.loadgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.DigiSepDeviceClassEnum;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupDigiSepCreateBuilder {

    public static class Builder {
        private Faker faker = new Faker();

        private static final String TYPE = "LM_GROUP_DIGI_SEP";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private int utilityEnrollmentGroup;
        private List<DigiSepDeviceClassEnum> deviceClassSet;
        private int rampInMinutes;
        private int rampOutMinutes;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG" + uuid);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDeviceClassSet(Optional<List<DigiSepDeviceClassEnum>> deviceClassSet) {
            List<DigiSepDeviceClassEnum> list = new ArrayList<>();
            list.add(LoadGroupEnums.DigiSepDeviceClassEnum.getRandomDeviceClassSet());

            this.deviceClassSet = deviceClassSet.orElse(list);
            return this;
        }

        public Builder withUtilityEnrollmentGroup(Optional<Integer> utilityEnrollmentGroup) {
            this.utilityEnrollmentGroup = utilityEnrollmentGroup.orElse(faker.number().numberBetween(1, 255));
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

        public Builder withRampInMinutes(Optional<Integer> rampInMinutes) {
            this.rampInMinutes = rampInMinutes.orElse(faker.number().numberBetween(-99999, 99999));
            return this;
        }

        public Builder withRampOutMinutes(Optional<Integer> rampOutMinutes) {
            this.rampOutMinutes = rampOutMinutes.orElse(faker.number().numberBetween(-99999, 99999));
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
            jo.put("utilityEnrollmentGroup", this.utilityEnrollmentGroup);
            jo.put("rampInMinutes", this.rampInMinutes);
            jo.put("rampOutMinutes", this.rampOutMinutes);
            jo.put("deviceClassSet", this.deviceClassSet);

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

    public static Builder buildLoadGroup() {
        return new LoadGroupDigiSepCreateBuilder.Builder(Optional.empty())
                .withDeviceClassSet(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRampInMinutes(Optional.empty())
                .withRampOutMinutes(Optional.empty())
                .withUtilityEnrollmentGroup(Optional.empty());
    }
}
