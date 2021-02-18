package com.eaton.builders.drsetup.gears;

import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class MeterDisconnectGearBuilder {

	public static class Builder {

		private Faker faker = new Faker();
		private static final String GEAR_TYPE = GearEnums.GearType.MeterDisconnect.getGearType();
		private String gearName;
		private Integer gearNumber;

		public Builder(Optional<String> gearName) {
			this.gearName = gearName.orElse("MeterDisGear " + faker.number().numberBetween(1, 100000));
		}

		public Builder withName(String gearName) {
			this.gearName = gearName;
			return this;
		}

		public Builder withGearNumber(Optional<Integer> gearNumber) {
			this.gearNumber = gearNumber.orElse(faker.number().numberBetween(1, 50));
			return this;
		}

		public JSONObject build() {
			JSONObject j = new JSONObject();
			j.put("gearName", this.gearName);
			j.put("gearNumber", this.gearNumber);
			j.put("controlMethod", GEAR_TYPE);

			JSONObject jo = new JSONObject();

			j.put("fields", jo);

			return j;
		}
	}

	public static Builder gearBuilder() {

		return new MeterDisconnectGearBuilder.Builder(Optional.empty()).withGearNumber(Optional.empty());

	}
}
