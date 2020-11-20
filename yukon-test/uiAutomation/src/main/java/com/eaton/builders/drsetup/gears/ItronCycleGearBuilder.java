package com.eaton.builders.drsetup.gears;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.point.PointRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class ItronCycleGearBuilder {
	public static class Builder {

        private Faker faker = new Faker();
        private static final String GEAR_TYPE = GearEnums.GearType.ItronCycle.getGearType();
        private String gearName;
        private Integer gearNumber;
        private String dutyCycleType;
        private Integer dutyCyclePercent;
        private Integer dutyCyclePeriodInMinutes;
        private Integer criticality;
        private Boolean mandatory;
        private Boolean rampIn;
        private Boolean rampOut;
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
        
        public Builder withDutyCycleType(Optional<GearEnums.DutyCycleType> dutyCycleType) {
            GearEnums.DutyCycleType randomDutyCycleType = dutyCycleType.orElse(GearEnums.DutyCycleType.StandardCycle);
            this.dutyCycleType = randomDutyCycleType.getDutyCycleType();
            return this;
        }

        public Builder withDutyCyclePercent(Optional<Integer> dutyCyclePercent) {
            this.dutyCyclePercent = dutyCyclePercent.orElse(faker.number().numberBetween(0, 100));
            return this;
        }
        
        public Builder withDutyCyclePeriodInMinutes(Optional<Integer> dutyCyclePeriodInMinutes) {
            this.dutyCyclePeriodInMinutes = dutyCyclePeriodInMinutes.orElse(15);
            return this;
        }
        
        public Builder withCriticality(Optional<Integer> criticality) {
            this.criticality = criticality.orElse(faker.number().numberBetween(0, 255));
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

        public Builder withRampIn(Optional<Boolean> rampIn) {
            this.rampIn = rampIn.orElse(false);
            return this;
        }

        public Builder withRampOut(Optional<Boolean> rampOut) {
            this.rampOut = rampOut.orElse(false);
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
            jo.put("dutyCycleType", this.dutyCycleType);
            jo.put("dutyCyclePercent", this.dutyCyclePercent);
            jo.put("dutyCyclePeriodInMinutes", this.dutyCyclePeriodInMinutes);
            jo.put("criticality", this.criticality);
            
            jo.put("mandatory", this.mandatory);

            jo.put("howToStopControl", this.howToStopControl);
            jo.put("rampIn", this.rampIn);
            jo.put("rampOut", this.rampOut);
            jo.put("capacityReduction", this.capacityReduction);
            JSONObject jo2 = new JSONObject();
            jo2.put("whenToChange", this.whenToChange);
            jo.put("whenToChangeFields", jo2);

            j.put("fields", jo);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = PointRequest.createPoint(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
	}
        
    public static Builder gearBuilder() {

    	return new ItronCycleGearBuilder.Builder(Optional.empty()).withGearNumber(Optional.empty())
    			.withDutyCycleType(Optional.empty())
    			.withDutyCyclePercent(Optional.empty())
    			.withDutyCyclePeriodInMinutes(Optional.empty())
    			.withCriticality(Optional.empty())
    			.withCapacityReduction(Optional.empty())
                .withMandatory(Optional.empty())
                .withHowToStopControl(Optional.empty())
                .withRampIn(Optional.empty())
                .withRampOut(Optional.empty())
                .withWhenToChange(Optional.empty());
    }
}
