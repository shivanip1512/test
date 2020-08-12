package com.eaton.builders.drsetup.loadgroup;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupEmetconCreateBuilder {
	public enum RelayUsage{
		RELAY_A("RELAY_A"),
		RELAY_B("RELAY_B"),
		RELAY_C("RELAY_C"),
		RELAY_ALL("RELAY_ALL");
		 private final String relayUsage;

		 RelayUsage(String relayUsage) {
	            this.relayUsage = relayUsage;
	        }

	        public String getRelayUsage() {
	            return this.relayUsage;
	        }

	        public static RelayUsage getRandomRelayUsage() {
	            Random random = new Random();
	            return values()[random.nextInt(values().length)];
	        }
		
	}
	
	public enum AddressUsage{
		GOLD("GOLD"),
		SILVER("SILVER");
		
		private final String addressUsage;

		AddressUsage(String addressUsage) {
	            this.addressUsage = addressUsage;
	        }

	        public String getAddressUsage() {
	            return this.addressUsage;
	        }

	        public static AddressUsage getRandomAddressUsage() {
	            Random random = new Random();
	            return values()[random.nextInt(values().length)];
	        }
		
	}
	
	public static class Builder {
	
		
        private Faker faker = new Faker();
        
        private static final String TYPE = "LM_GROUP_EMETCON";
        private String name;
        private int routeId;
        private int goldAddress;
        private int silverAddress;
        private String addressUsage;
        private String relayUsage;
        private double kwCapacity;
        private boolean disableGroup;
        private boolean disableControl;   
        
        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();            
            String uuid = u.replace("-", "");
            
            this.name = name.orElse("AT LG " + uuid);
        }

        public Builder withGoldAddress(Optional<Integer> goldAddress) {
            this.goldAddress = goldAddress.orElse(faker.number().numberBetween(0, 4));            
            return this;
        }

        public Builder withSilverAddress(Optional<Integer> silverAddress) {
            this.silverAddress = silverAddress.orElse(faker.number().numberBetween(0, 60));            
            return this;
        }
        
        public Builder withCommunicationRoute(int routeId) {
        	this.routeId = routeId;
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
        
        public Builder withRelayUsage(Optional<LoadGroupEmetconCreateBuilder.RelayUsage> relayUsage) {
        	LoadGroupEmetconCreateBuilder.RelayUsage randomRelayUsage = relayUsage.orElse(LoadGroupEmetconCreateBuilder.RelayUsage.getRandomRelayUsage());

            this.relayUsage = randomRelayUsage.getRelayUsage();
            return this;
        }
        
        public Builder withAddressUsage(Optional<LoadGroupEmetconCreateBuilder.AddressUsage> addressUsage) {
        	LoadGroupEmetconCreateBuilder.AddressUsage randomAddressUsage = addressUsage.orElse(LoadGroupEmetconCreateBuilder.AddressUsage.getRandomAddressUsage());

            this.addressUsage = randomAddressUsage.getAddressUsage();
            return this;
        }
       
        public JSONObject build() {            
            JSONObject j = new JSONObject();            
            
            JSONObject jo = new JSONObject();            
            jo.put("name", this.name);
            jo.put("type", TYPE);
            jo.put("routeId", this.routeId);
            jo.put("goldAddress", this.goldAddress);
            jo.put("silverAddress", this.silverAddress);
            jo.put("addressUsage", this.addressUsage);
            jo.put("relayUsage", this.relayUsage);
            jo.put("kWCapacity", this.kwCapacity);
            jo.put("disableGroup", this.disableGroup);
            jo.put("disableControl", this.disableControl);            

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
