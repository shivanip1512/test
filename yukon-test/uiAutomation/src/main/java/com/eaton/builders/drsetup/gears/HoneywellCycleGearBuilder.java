package com.eaton.builders.drsetup.gears;

import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class HoneywellCycleGearBuilder {
	public static class Builder {

        private Faker faker = new Faker();
        private static final String GEAR_TYPE = GearEnums.GearType.HoneywellCycle.getGearType();
        private String gearName;
        private Integer gearNumber;
        private Boolean mandatory;
        private Boolean rampInOut;
        private Integer controlPercent;
        private Integer cyclePeriodInMinutes;
        private String howToStopControl;
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

        public Builder withRampInOut(Optional<Boolean> rampInOut) {
            this.rampInOut = rampInOut.orElse(false);
            return this;
        }
        
        public Builder withCyclePeriodInMinutes(Optional<Integer> cyclePeriodInMinutes) {
        	this.cyclePeriodInMinutes = cyclePeriodInMinutes.orElse(30);
        	return this;
        }

        public Builder withControlPercent(Optional<Integer> controlPercent) {
            this.controlPercent = controlPercent.orElse(faker.number().numberBetween(5, 100));
            return this;
        }

        public Builder withHowToStopControl(Optional<GearEnums.HowToStopControl> howToStopControl) {
            GearEnums.HowToStopControl randomHowToStopControl = howToStopControl.orElse(GearEnums.HowToStopControl.Restore);
            this.howToStopControl = randomHowToStopControl.getHowToStopControl();
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
            jo.put("mandatory", this.mandatory);
            jo.put("controlPercent", this.controlPercent);
            jo.put("cyclePeriodInMinutes", this.cyclePeriodInMinutes);
            jo.put("howToStopControl", this.howToStopControl);
            jo.put("rampInOut", this.rampInOut);
            jo.put("capacityReduction", this.capacityReduction);
            JSONObject jo2 = new JSONObject();
            jo2.put("whenToChange", this.whenToChange);
            jo.put("whenToChangeFields", jo2);

            j.put("fields", jo);

            return j;
        }
    }

    public static Builder gearBuilder() {

        return new HoneywellCycleGearBuilder.Builder(Optional.empty()).withGearNumber(Optional.empty())
                .withCapacityReduction(Optional.empty())
                .withControlPercent(Optional.empty())
                .withHowToStopControl(Optional.empty())
                .withMandatory(Optional.empty())
                .withCyclePeriodInMinutes(Optional.empty())
                .withRampInOut(Optional.empty())
                .withWhenToChange(Optional.empty());
    }
}