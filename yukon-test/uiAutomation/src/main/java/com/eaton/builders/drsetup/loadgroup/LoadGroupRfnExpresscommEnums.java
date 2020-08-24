package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupRfnExpresscommEnums {
	
	public enum AddressUsageRfnExpresscomm {
		GEO("GEO"), 
		SUBSTATION("SUBSTATION"), 
		FEEDER("FEEDER"), 
		ZIP("ZIP"),
		USER("USER"),
		SERIAL("SERIAL"),
		LOAD("LOAD"), 
		SPLINTER("SPLINTER"), 
		PROGRAM("PROGRAM");;
		
		private final String addressUsage;

		AddressUsageRfnExpresscomm(String addressUsage) {
			this.addressUsage = addressUsage;
		}

		public String getAddressUsage() {
			return this.addressUsage;
		}

		public static AddressUsageRfnExpresscomm getRandomAddressUsage() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}

	public enum RelayUsageRfnExpresscomm {
		RELAY_1("Load_1"), 
		RELAY_2("Load_2"), 
		RELAY_3("Load_3"), 
		RELAY_4("Load_4"),
		RELAY_5("Load_5"), 
		RELAY_6("Load_6"), 
		RELAY_7("Load_7"), 
		RELAY_8("Load_8");

		private final String relayUsage;

		RelayUsageRfnExpresscomm(String relayUsage) {
			this.relayUsage = relayUsage;
		}

		public String getRelayUsage() {
			return this.relayUsage;
		}

		public static RelayUsageRfnExpresscomm getRandomRelayUsage() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}
	
	public enum ProtocolPriorityRfnExpresscomm {
		DEFAULT("DEFAULT"), 
		MEDIUM("MEDIUM"), 
		HIGH("HIGH"),
		HIGHEST("HIGHEST");
		
		private final String protocolPriority;

		ProtocolPriorityRfnExpresscomm(String protocolPriority) {
			this.protocolPriority = protocolPriority;
		}

		public String getProtocolPriority() {
			return this.protocolPriority;
		}

		public static ProtocolPriorityRfnExpresscomm getRandomProtocolPriority() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}
}
