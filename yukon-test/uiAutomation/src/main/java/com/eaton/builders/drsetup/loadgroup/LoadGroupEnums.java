package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;
public class LoadGroupEnums {
	
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
	
}
