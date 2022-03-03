package com.eaton.builders.tools.points;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class PointCreateService {

    public static Pair<JSONObject, JSONObject> createAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new AnalogPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> createStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new StatusPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> createCalcAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new CalcAnalogPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .create();
    }

    public static Pair<JSONObject, JSONObject> createCalcStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new CalcStatusPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> createPulseAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new PulseAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> createDemandAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new DemandAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }
}
