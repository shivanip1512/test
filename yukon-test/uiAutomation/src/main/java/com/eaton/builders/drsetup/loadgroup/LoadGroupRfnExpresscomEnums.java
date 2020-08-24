package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public class LoadGroupRfnExpresscomEnums {
	
	public enum AddressUsageRfnExpresscom {
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

		AddressUsageRfnExpresscom(String addressUsage) {
			this.addressUsage = addressUsage;
		}

		public String getAddressUsage() {
			return this.addressUsage;
		}

		public static AddressUsageRfnExpresscom getRandomAddressUsage() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}

	public enum RelayUsageRfnExpresscom {
		Load_1("Load_1"), 
		Load_2("Load_2"), 
		Load_3("Load_3"), 
		Load_4("Load_4"),
		Load_5("Load_5"), 
		Load_6("Load_6"), 
		Load_7("Load_7"), 
		Load_8("Load_8");

		private final String relayUsage;

		RelayUsageRfnExpresscom(String relayUsage) {
			this.relayUsage = relayUsage;
		}

		public String getRelayUsage() {
			return this.relayUsage;
		}

		public static RelayUsageRfnExpresscom getRandomRelayUsage() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}
	
	public enum ProtocolPriorityRfnExpresscom {
		DEFAULT("DEFAULT"), 
		MEDIUM("MEDIUM"), 
		HIGH("HIGH"),
		HIGHEST("HIGHEST");
		
		private final String protocolPriority;

		ProtocolPriorityRfnExpresscom(String protocolPriority) {
			this.protocolPriority = protocolPriority;
		}

		public String getProtocolPriority() {
			return this.protocolPriority;
		}

		public static ProtocolPriorityRfnExpresscom getRandomProtocolPriority() {

			Random random = new Random();
			return values()[random.nextInt(values().length)];
		}
	}
}
