package com.eaton.builders.drsetup.loadgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.AddressUsageVersacom;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RelayUsage;
import com.eaton.framework.TestDbDataType;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupVersacomCreateBuilder {

    public static class Builder {
        private Faker faker = new Faker();

        private static final String TYPE = "LM_GROUP_VERSACOM";
        private String name;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private Integer routeId;
        private Integer utilityAddress;
        private Integer sectionAddress;
        private Integer serialAddress;
        private String classAddress;
        private String divisionAddress;
        List<LoadGroupEnums.AddressUsageVersacom> addressUsageList = new ArrayList<>();
        private List<RelayUsage> relayUsage;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withRouteId(Optional<TestDbDataType.CommunicationRouteData> routeId) {
            TestDbDataType.CommunicationRouteData randomRelayUsage = routeId.orElse(TestDbDataType.CommunicationRouteData.getRandomRouteId());

            this.routeId = randomRelayUsage.getId();
            return this;
        }

        public Builder withSection(Optional<Integer> sectionAddress) {
            this.sectionAddress = sectionAddress.orElse(faker.number().numberBetween(0, 256));
            addressUsageList.add(LoadGroupEnums.AddressUsageVersacom.SECTION);
            return this;
        }        

        public Builder withClass(Optional<String> classAddress) {
            int x = faker.number().numberBetween(1, 65535);
            String s = String.format("%16s", Integer.toBinaryString(x)).replace(' ', '0');
            this.classAddress = classAddress.orElse(s);
            addressUsageList.add(LoadGroupEnums.AddressUsageVersacom.CLASS);
            return this;
        }

        public Builder withDivision(Optional<String> divisionAddress) {
            int x = faker.number().numberBetween(1, 65535);
            String s = String.format("%16s", Integer.toBinaryString(x)).replace(' ', '0');
            this.divisionAddress = divisionAddress.orElse(s);
            addressUsageList.add(LoadGroupEnums.AddressUsageVersacom.DIVISION);
            return this;
        }
        
        public Builder withSerial(Optional<Integer> serialAddress) {
            this.serialAddress = serialAddress.orElse(faker.number().numberBetween(1, 99999));
            addressUsageList.add(LoadGroupEnums.AddressUsageVersacom.SERIAL);
            return this;
        }
        
        public Builder withUtilityAddress(Optional<Integer> utilityAddress) {
            this.utilityAddress = utilityAddress.orElse(faker.number().numberBetween(1, 254));
            return this;
        }
        
        public Builder withRelayUsage(Optional<List<RelayUsage>> relayUsage) {
            List<RelayUsage> relays = new ArrayList<>();
            relays.add(RelayUsage.getRandomRelayUsage());

            this.relayUsage = relayUsage.orElse(relays);
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

        public JSONObject build() {
            JSONObject j = new JSONObject();

            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("type", TYPE);
            jo.put("kWCapacity", this.kwCapacity);
            jo.put("disableGroup", this.disableGroup);
            jo.put("disableControl", this.disableControl);
            jo.put("utilityAddress", this.utilityAddress);
            jo.put("sectionAddress", this.sectionAddress);
            jo.put("serialAddress", this.serialAddress);
            jo.put("classAddress", this.classAddress);
            jo.put("divisionAddress", this.divisionAddress);
            jo.put("addressUsage", this.addressUsageList);
            jo.put("relayUsage", this.relayUsage);
            jo.put("routeId", this.routeId);
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

    public static Builder buildDefaultVersacomLoadGroup() {
        List<AddressUsageVersacom> list = new ArrayList<>();
        list.add(AddressUsageVersacom.UTILITY);
        return new LoadGroupVersacomCreateBuilder.Builder(Optional.empty())
                .withRouteId(Optional.empty())
                .withSection(Optional.empty())
                .withUtilityAddress(Optional.empty())
                .withRelayUsage(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty());
    }
}
