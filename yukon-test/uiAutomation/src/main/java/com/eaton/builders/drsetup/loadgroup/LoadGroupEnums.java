package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupEnums {

	public enum RelayUsageMCT {
		RELAY_1("RELAY_1"), 
		RELAY_2("RELAY_2"), 
		RELAY_3("RELAY_3"), 
		RELAY_4("RELAY_4");

		private final String relayUsage;

		RelayUsageMCT(String relayUsage) {
			this.relayUsage = relayUsage;
		}

		public String getRelayUsage() {
			return this.relayUsage;
		}

		public static RelayUsageMCT getRandomRelayUsage() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}

	public enum AddressLevelMCT {
		BRONZE("BRONZE"), 
		LEAD("LEAD"), 
		MCT_ADDRESS("MCT_ADDRESS");

		private final String addressLevelMCT;

		AddressLevelMCT(String addressLevelMCT) {
			this.addressLevelMCT = addressLevelMCT;
		}

		public String getAddressLevelMCT() {
			return this.addressLevelMCT;
		}

		public static AddressLevelMCT getRandomAddressLevelMCT() {
			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}

	}

}
