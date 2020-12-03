package com.eaton.builders.drsetup.loadgroup;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RippleAreaCode;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RippleGroup;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RippleShedTime;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.TestDbDataType.CommunicationRouteData;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRippleCreateBuilder {
    public static class Builder {
        private Faker faker = new Faker();

        private static final String TYPE = "LM_GROUP_RIPPLE";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private Integer routeId;
        private String group;
        private String shedTime;
        private String areaCode;
        private String control;
        private String restore;

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

        public Builder withRouteId(Optional<TestDbDataType.CommunicationRouteData> routeId) {
            TestDbDataType.CommunicationRouteData randomRelayUsage = routeId.orElse(CommunicationRouteData.getRandomRouteId());

            this.routeId = randomRelayUsage.getId();
            return this;
        }

        public Builder withControl(Optional<String> control) {
            // 2,147,483,647 is equal to max value for 32 bit no
            int x = faker.number().numberBetween(0, 2147483647);
            String s = String.format("%34s", Integer.toBinaryString(x)).replace(' ', '0');
            this.control = control.orElse(s);
            return this;
        }

        public Builder withRestore(Optional<String> restore) {
            int x = faker.number().numberBetween(0, 2147483647);
            String s = String.format("%34s", Integer.toBinaryString(x)).replace(' ', '0');
            this.restore = restore.orElse(s);
            return this;
        }

        public Builder withAreaCode(Optional<RippleAreaCode> areaCode) {
            RippleAreaCode randomAreaCode = areaCode.orElse(RippleAreaCode.getRandomAreaCode());

            this.areaCode = randomAreaCode.getAreaCode();
            return this;
        }

        public Builder withGroup(Optional<RippleGroup> group) {
            RippleGroup randomGroup = group.orElse(RippleGroup.getRandomGroup());

            this.group = randomGroup.getGroup();
            return this;
        }

        public Builder withShedTime(Optional<RippleShedTime> shedTime) {
            RippleShedTime randomShedTime = shedTime
                    .orElse(RippleShedTime.getRandomShedTime());

            this.shedTime = randomShedTime.getShedTime();
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
            jo.put("routeId", this.routeId);
            jo.put("control", this.control);
            jo.put("restore", this.restore);
            jo.put("shedTime", this.shedTime);
            jo.put("areaCode", this.areaCode);
            jo.put("group", this.group);
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

    public static Builder buildDefaultRippleLoadGroup() {
        return new LoadGroupRippleCreateBuilder.Builder(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRouteId(Optional.empty())
                .withControl(Optional.empty())
                .withRestore(Optional.empty())
                .withShedTime(Optional.empty())
                .withAreaCode(Optional.empty())
                .withGroup(Optional.empty());
    }
}
