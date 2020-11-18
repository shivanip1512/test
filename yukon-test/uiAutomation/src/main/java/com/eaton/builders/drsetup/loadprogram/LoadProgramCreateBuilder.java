package com.eaton.builders.drsetup.loadprogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.eaton.builders.drsetup.loadprogram.ProgramEnums.ProgramType;
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadProgramCreateBuilder {

    public static class Builder {

        private Faker faker = new Faker();
        private ProgramType programType;
        private String type;
        private String name;
        private String operationalState;
        private int constraintId;
        private String constraintName;
        private Double triggerOffset;
        private Double restoreOffset;
        private int controlWindowOneAvailableStartTimeInMinutes;
        private int controlWindowOneAvailableStopTimeInMinutes;
        private int controlWindowTwoAvailableStartTimeInMinutes;
        private int controlWindowTwoAvailableStopTimeInMinutes;
        private List<JSONObject> gears = new ArrayList<>();
        private List<Integer> assignedGroupIds = new ArrayList<>();
        private int groupOrder = 1;

        public Builder(ProgramEnums.ProgramType type, List<JSONObject> gears, List<Integer> assignedGroupIds) {
            this.programType = type;
            this.type = programType.getProgramType();
            this.gears = gears;
            this.assignedGroupIds = assignedGroupIds;
        }

        public Builder withName(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse(programType.getDesc() + uuid);
            return this;
        }

        public Builder withOperationalState(Optional<ProgramEnums.OperationalState> operationalState) {
            ProgramEnums.OperationalState opState = operationalState
                    .orElse(ProgramEnums.OperationalState.getRandomOperationalState());
            this.operationalState = opState.getOperationalState();
            return this;
        }

        public Builder withConstraintId(Optional<Integer> constraintId) {
            this.constraintId = constraintId.orElse(0);
            return this;
        }

        public Builder withConstraintName(Optional<String> constraintName) {
            this.constraintName = constraintName.orElse("Default Constraint");
            return this;
        }

        public Builder withTriggerOffset(Optional<Double> triggerOffset) {
            this.triggerOffset = triggerOffset.orElse(faker.number().randomDouble(3, 0, 99999));
            return this;
        }

        public Builder withRestoreOffset(Optional<Double> restoreOffset) {
            this.restoreOffset = restoreOffset.orElse(faker.number().randomDouble(3, -9999, 99999));
            return this;
        }

        public Builder withControlWindowOneAvailableStartTimeInMinutes(
                Optional<Integer> controlWindowOneAvailableStartTimeInMinutes) {
            this.controlWindowOneAvailableStartTimeInMinutes = controlWindowOneAvailableStartTimeInMinutes
                    .orElse(faker.number().numberBetween(0, 1439));
            return this;
        }

        public Builder withcontrolWindowOneAvailableStopTimeInMinutes(
                Optional<Integer> controlWindowOneAvailableStopTimeInMinutes) {
            this.controlWindowOneAvailableStopTimeInMinutes = controlWindowOneAvailableStopTimeInMinutes
                    .orElse(faker.number().numberBetween(0, 1439));
            return this;
        }

        public Builder withcontrolWindowTwoAvailableStartTimeInMinutes(
                Optional<Integer> controlWindowTwoAvailableStartTimeInMinutes) {
            this.controlWindowTwoAvailableStartTimeInMinutes = controlWindowTwoAvailableStartTimeInMinutes
                    .orElse(faker.number().numberBetween(0, 1439));
            return this;
        }

        public Builder withcontrolWindowTwoAvailableStopTimeInMinutes(
                Optional<Integer> controlWindowTwoAvailableStopTimeInMinutes) {
            this.controlWindowTwoAvailableStopTimeInMinutes = controlWindowTwoAvailableStopTimeInMinutes
                    .orElse(faker.number().numberBetween(0, 1439));
            return this;
        }

        public Builder withAssignedGroupId(List<Integer> assignedGroupIds) {
            this.assignedGroupIds = assignedGroupIds;
            return this;
        }

        public Builder withGears(List<JSONObject> gears) {
            this.gears = gears;
            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("name", this.name);
            j.put("type", this.type);
            j.put("operationalState", this.operationalState);
            j.put("triggerOffset", this.triggerOffset);
            j.put("restoreOffset", this.restoreOffset);

            JSONArray gearArray = new JSONArray();
            for (JSONObject gear : gears) {
                gearArray.put(gear);
            }
            j.put("gears", gearArray);

            JSONObject constraint = new JSONObject();
            constraint.put("constraintId", this.constraintId);
            if (this.constraintName != null) {
                constraint.put("constraintName", this.constraintName);
            }
            j.put("constraint", constraint);

            JSONArray groupArray = new JSONArray();
            for (int assignedGroup : assignedGroupIds) {
                JSONObject s = new JSONObject();
                s.put("groupId", assignedGroup);
                s.put("groupOrder", groupOrder);
                groupArray.put(s);
                groupOrder++;
            }
            j.put("assignedGroups", groupArray);

            JSONObject controlWindow = new JSONObject();

            JSONObject controlWindowOne = new JSONObject();
            controlWindowOne.put("availableStartTimeInMinutes", this.controlWindowOneAvailableStartTimeInMinutes);
            controlWindowOne.put("availableStopTimeInMinutes", this.controlWindowOneAvailableStopTimeInMinutes);

            JSONObject controlWindowTwo = new JSONObject();
            controlWindowTwo.put("availableStartTimeInMinutes", this.controlWindowTwoAvailableStartTimeInMinutes);
            controlWindowTwo.put("availableStopTimeInMinutes", this.controlWindowTwoAvailableStopTimeInMinutes);

            controlWindow.put("controlWindowOne", controlWindowOne);
            controlWindow.put("controlWindowTwo", controlWindowTwo);

            j.put("controlWindow", controlWindow);

            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = DrSetupCreateRequest.createLoadProgram(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }

    public static Builder buildLoadProgram(ProgramEnums.ProgramType type, List<JSONObject> gears,
            List<Integer> assignedGroupIds) {
        return new LoadProgramCreateBuilder.Builder(type, gears, assignedGroupIds).withName(Optional.empty())
                .withOperationalState(Optional.empty())
                .withConstraintId(Optional.empty())
                .withTriggerOffset(Optional.empty())
                .withRestoreOffset(Optional.empty())
                .withControlWindowOneAvailableStartTimeInMinutes(Optional.empty())
                .withcontrolWindowOneAvailableStopTimeInMinutes(Optional.empty())
                .withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.empty())
                .withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.empty());
    }
}
