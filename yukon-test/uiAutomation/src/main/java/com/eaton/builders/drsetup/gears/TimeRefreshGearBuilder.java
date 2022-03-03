package com.eaton.builders.drsetup.gears;

import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class TimeRefreshGearBuilder {
	public static class Builder {
		
		private Faker faker = new Faker();
		private static final String GEAR_TYPE = GearEnums.GearType.TimeRefresh.getGearType();
		private String gearName;
		private Integer gearNumber;
		private Boolean mandatory;
		private String refreshShedType;
		private Integer shedTime;
		private Integer numberOfGroups;
		private Integer sendRate;
		private String groupSelectionMethod;
		private Boolean rampIn;
		private Boolean rampOut;
		private String howToStopControl;
		private Integer stopCommandRepeat;
		private Integer capacityReduction;
		private String whenToChange;

		public Builder(Optional<String> gearName) {
			this.gearName = gearName.orElse("Gear " + faker.number().numberBetween(1, 100000));
		}

		public Builder withName(String gearName) {

			this.gearName = gearName;
			return this;
		}

		public Builder withGearNumber(Optional<Integer> gearNumber) {
			this.gearNumber = gearNumber.orElse(faker.number().numberBetween(1, 50));
			return this;
		}

		public Builder withMandatory(Optional<Boolean> mandatory) {
			this.mandatory = mandatory.orElse(false);
			return this;
		}

		public Builder withRefreshShedType(Optional<GearEnums.RefreshShedType> refreshShedType) {
			GearEnums.RefreshShedType randomRefreshShedType = refreshShedType
					.orElse(GearEnums.RefreshShedType.FixedShedTime);
			this.refreshShedType = randomRefreshShedType.getRefreshShedType();
			return this;
		}

		public Builder withShedTime(Optional<Integer> shedTime) {
			this.shedTime = shedTime.orElse(60);
			return this;
		}

		public Builder withnumberOfGroups(Optional<Integer> numberOfGroups) {
			this.numberOfGroups = numberOfGroups.orElse(faker.number().numberBetween(0, 25));
			return this;
		}

		public Builder withsendRate(Optional<Integer> sendRate) {
			this.sendRate = sendRate.orElse(60);
			return this;
		}

		public Builder withGroupSelectionMethod(Optional<GearEnums.GroupSelectionMethod> groupSelectionMethod) {
			GearEnums.GroupSelectionMethod randomGroupSelectionMethod = groupSelectionMethod
					.orElse(GearEnums.GroupSelectionMethod.AlwaysFirstGroup);
			this.groupSelectionMethod = randomGroupSelectionMethod.getGroupSelectionMethod();
			return this;
		}

		public Builder withRampIn(Optional<Boolean> rampIn) {
			this.rampIn = rampIn.orElse(false);
			return this;
		}

		public Builder withRampOut(Optional<Boolean> rampOut) {
			this.rampOut = rampOut.orElse(false);
			return this;
		}

		public Builder withHowToStopControl(Optional<GearEnums.HowToStopControl> howToStopControl) {
			GearEnums.HowToStopControl randomHowToStopControl = howToStopControl
					.orElse(GearEnums.HowToStopControl.Restore);
			this.howToStopControl = randomHowToStopControl.getHowToStopControl();
			return this;
		}

		public Builder withStopCommandRepeat(Optional<Integer> stopCommandRepeat) {
			this.stopCommandRepeat = stopCommandRepeat.orElse(faker.number().numberBetween(0, 5));
			return this;
		}

		public Builder withCapacityReduction(Optional<Integer> capacityReduction) {
			this.capacityReduction = capacityReduction.orElse(faker.number().numberBetween(0, 100));
			return this;
		}

		public Builder withWhenToChange(Optional<GearEnums.WhenToChange> whenToChange) {
			GearEnums.WhenToChange randomWhenToChange = whenToChange.orElse(GearEnums.WhenToChange.ManualOnly);
			this.whenToChange = randomWhenToChange.getWhenToChange();
			return this;
		}

	    public JSONObject build() {
	        JSONObject j = new JSONObject();
	        j.put("gearName", this.gearName);
	        j.put("gearNumber", this.gearNumber);
	        j.put("controlMethod", GEAR_TYPE);
	
	        JSONObject jo = new JSONObject();
	        jo.put("refreshShedType", this.refreshShedType);
	        jo.put("shedTime", this.shedTime);
	        jo.put("numberOfGroups", this.numberOfGroups);
	        jo.put("sendRate", this.sendRate);
	        jo.put("groupSelectionMethod", this.groupSelectionMethod);
	        
	        jo.put("mandatory", this.mandatory);
	
	        jo.put("howToStopControl", this.howToStopControl);
	        jo.put("stopCommandRepeat", this.stopCommandRepeat);
	        jo.put("rampIn", this.rampIn);
	        jo.put("rampOut", this.rampOut);
	        jo.put("capacityReduction", this.capacityReduction);
	        JSONObject jo2 = new JSONObject();
	        jo2.put("whenToChange", this.whenToChange);
	        jo.put("whenToChangeFields", jo2);
	
	        j.put("fields", jo);
	
	        return j;
	    }
	}
	    
	public static Builder gearBuilder() {	
		return new TimeRefreshGearBuilder.Builder(Optional.empty()).withGearNumber(Optional.empty())
				.withMandatory(Optional.empty())
				.withRefreshShedType(Optional.empty())
				.withShedTime(Optional.empty())
				.withnumberOfGroups(Optional.empty())
				.withsendRate(Optional.empty())
				.withGroupSelectionMethod(Optional.empty())
				.withRampIn(Optional.empty())
				.withRampOut(Optional.empty())
				.withHowToStopControl(Optional.empty())
				.withStopCommandRepeat(Optional.empty())
				.withCapacityReduction(Optional.empty())
				.withWhenToChange(Optional.empty());
	}   
}
