package com.eaton.builders.drsetup.loadgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.AddressUsage;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RelayUsage;
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
        private List<AddressUsage> addressUsage;
        private List<RelayUsage> relayUsage;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withKwCapacity(Optional<Double> kwCapacity) {
            this.kwCapacity = kwCapacity.orElse(faker.number().randomDouble(3, 0, 99999));
            return this;
        }

        public Builder withUtilityAddress(Optional<Integer> utilityAddress) {
            this.utilityAddress = utilityAddress.orElse(faker.number().numberBetween(1, 254));
            return this;
        }

        public Builder withSectionAddress(Optional<Integer> sectionAddress) {
            this.sectionAddress = sectionAddress.orElse(faker.number().numberBetween(0, 256));
            return this;
        }

        public Builder withSerialAddress(Optional<Integer> serialAddress) {
            this.serialAddress = serialAddress.orElse(faker.number().numberBetween(1, 99999));
            return this;
        }

        public Builder withClassAddress(Optional<String> classAddress) {
            int x = faker.number().numberBetween(1, 65535);
            String s = String.format("%16s", Integer.toBinaryString(x)).replace(' ', '0');
            this.classAddress = classAddress.orElse(s);
            return this;
        }

        public Builder withDivisionAddress(Optional<String> divisionAddress) {
            int x = faker.number().numberBetween(1, 65535);
            String s = String.format("%16s", Integer.toBinaryString(x)).replace(' ', '0');
            this.divisionAddress = divisionAddress.orElse(s);
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

        public Builder withRouteId(Optional<LoadGroupEnums.RouteId> route_Id) {
            LoadGroupEnums.RouteId randomRelayUsage = route_Id
                    .orElse(LoadGroupEnums.RouteId.getRandomRouteId());

            this.routeId = randomRelayUsage.getRouteId();
            return this;
        }

        public Builder withRelayUsage(Optional<RelayUsage> relayUsage) {
            List<RelayUsage> relays = new ArrayList<RelayUsage>();

            if (relayUsage.isEmpty()) {
                relays.add(RelayUsage.RELAY_1);
                relays.add(RelayUsage.RELAY_2);
                relays.add(RelayUsage.RELAY_3);
                relays.add(RelayUsage.RELAY_4);
                this.relayUsage = relays;
            } else {
                relays.add(relayUsage.get());
                this.relayUsage = relays;
            }
            return this;
        }

        public Builder withOtherAddressUsage(Optional<AddressUsage> addressUsage) {
            List<AddressUsage> versacomAddressUsage = new ArrayList<AddressUsage>();

            if (addressUsage.isEmpty()) {
                versacomAddressUsage.add(AddressUsage.UTILITY);
                versacomAddressUsage.add(AddressUsage.SECTION);
                versacomAddressUsage.add(AddressUsage.CLASS);
                versacomAddressUsage.add(AddressUsage.DIVISION);
                this.addressUsage = versacomAddressUsage;
            } else {
                versacomAddressUsage.add(addressUsage.get());
                this.addressUsage = versacomAddressUsage;
            }
            return this;
        }

        public Builder withSerialAddressUsage(Optional<AddressUsage> addressUsage) {
            List<AddressUsage> versacomAddressUsage = new ArrayList<AddressUsage>();

            if (addressUsage.isEmpty()) {
                versacomAddressUsage.add(AddressUsage.UTILITY);
                versacomAddressUsage.add(AddressUsage.SERIAL);
                this.addressUsage = versacomAddressUsage;
            } else {
                versacomAddressUsage.add(addressUsage.get());
                this.addressUsage = versacomAddressUsage;
            }
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
            jo.put("addressUsage", this.addressUsage);
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
        return new LoadGroupVersacomCreateBuilder.Builder(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withRelayUsage(Optional.empty())
                .withOtherAddressUsage(Optional.empty())
                .withClassAddress(Optional.empty())
                .withDivisionAddress(Optional.empty())
                .withRouteId(Optional.empty())
                .withSectionAddress(Optional.empty())
                .withUtilityAddress(Optional.empty())
                .withSerialAddress(Optional.empty());
    }
}
