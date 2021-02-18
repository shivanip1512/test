package com.eaton.builders.drsetup.loadgroup;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRfnExpresscomCreateBuilder {
    public static class Builder {
        private Faker faker = new Faker();
        private static final String PARENTTYPE = "LM_GROUP_EXPRESSCOMM";
        private String name;
        private int spid;
        private int geo;
        private int substation;
        private String feeder;
        private int zip;
        private int user;
        private int serialAddress;
        private int program;
        private int splinter;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;
        private List<String> relayUsage;
        private String protocolPriority;
        List<LoadGroupEnums.AddressUsageExpresscom> addressUsageList = new ArrayList<>();

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT LG" + uuid);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSpid(Optional<Integer> serviceProvider) {
            this.spid = serviceProvider.orElse(faker.number().numberBetween(1, 65534));
            return this;
        }

        public Builder withGeo(Optional<Integer> geo) {
            this.geo = geo.orElse(faker.number().numberBetween(1, 65534));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.GEO);
            return this;
        }

        public Builder withUser(Optional<Integer> user) {
            this.user = user.orElse(faker.number().numberBetween(1, 65534));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.USER);
            return this;
        }

        public Builder withSubstation(Optional<Integer> substation) {
            this.substation = substation.orElse(faker.number().numberBetween(1, 65534));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.SUBSTATION);
            return this;
        }

        public Builder withZip(Optional<Integer> zip) {
            this.zip = zip.orElse(faker.number().numberBetween(1, 16777214));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.ZIP);
            return this;
        }

        public Builder withFeeder(Optional<String> feeder) {
            int x = faker.number().numberBetween(1, 65535);
            String s = String.format("%16s", Integer.toBinaryString(x)).replace(' ', '0');
            this.feeder = feeder.orElse(s);
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.FEEDER);
            return this;
        }
        
        public Builder withSerial(Optional<Integer> serialAddress) {
            this.serialAddress = serialAddress.orElse(faker.number().numberBetween(1, 999999));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.SERIAL);
            return this;
        }
        
        public Builder withRelayUsage(Optional<List<String>> relayUsage) {
            List<String> relays = new ArrayList<>();
            relays.add(LoadGroupEnums.RelayUsageExpresscom.getRandomRelayUsage());
            this.relayUsage = relayUsage.orElse(relays);
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.LOAD);
            return this;
        }

        public Builder withProgram(Optional<Integer> program) {
            this.program = program.orElse(faker.number().numberBetween(1, 254));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.PROGRAM);
            return this;
        }

        public Builder withSplinter(Optional<Integer> splinter) {
            this.splinter = splinter.orElse(faker.number().numberBetween(1, 254));
            addressUsageList.add(LoadGroupEnums.AddressUsageExpresscom.SPLINTER);
            return this;
        }

        public Builder withKwCapacity(Optional<Double> kwCapacity) {
            this.kwCapacity = kwCapacity.orElse(faker.number().randomDouble(2, 0, 99999));
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

        public Builder withProtocolPriority(Optional<LoadGroupEnums.ProtocolPriorityExpresscom> protocolPriority) {
            LoadGroupEnums.ProtocolPriorityExpresscom randomProtocolPriority = protocolPriority
                    .orElse(LoadGroupEnums.ProtocolPriorityExpresscom.getRandomProtocolPriority());

            this.protocolPriority = randomProtocolPriority.getProtocolPriority();
            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();

            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            jo.put("type", "LM_GROUP_RFN_EXPRESSCOMM");
            jo.put("serviceProvider", this.spid);
            jo.put("geo", this.geo);
            jo.put("substation", this.substation);
            jo.put("feeder", this.feeder);
            jo.put("zip", this.zip);
            jo.put("user", this.user);
            jo.put("serialNumber", this.serialAddress);
            jo.put("program", this.program);
            jo.put("splinter", this.splinter);
            jo.put("addressUsage", this.addressUsageList);
            jo.put("relayUsage", this.relayUsage);
            jo.put("protocolPriority", this.protocolPriority);
            jo.put("kWCapacity", this.kwCapacity);
            jo.put("disableGroup", this.disableGroup);
            jo.put("disableControl", this.disableControl);

            j.put(PARENTTYPE, jo);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();

            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadGroup(request.toString());

            String id = createResponse.path("groupId").toString();

            ExtractableResponse<?> er = DrSetupGetRequest.getLoadGroup(Integer.parseInt(id));

            String res = er.asString();
            JSONObject response = new JSONObject(res);
            JSONObject jsonResponse = response.getJSONObject(PARENTTYPE);

            return new Pair<>(request, jsonResponse);
        }

    }

    public static Builder buildDefaultRfnExpresscomLoadGroup() {
        return new LoadGroupRfnExpresscomCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withDisableControl(Optional.empty())
                .withSpid(Optional.empty())
                .withGeo(Optional.empty())
                .withSubstation(Optional.empty())
                .withFeeder(Optional.empty())
                .withZip(Optional.empty())
                .withProgram(Optional.empty())
                .withSplinter(Optional.empty())
                .withRelayUsage(Optional.empty())
                .withProtocolPriority(Optional.empty());
    }
}