package com.eaton.builders.tools.points;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class PointCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new AnalogPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new StatusPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateCalcAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new CalcAnalogPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateCalcStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new CalcStatusPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreatePulseAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new PulseAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateDemandAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name, Optional<Integer> offSet) {
        return new DemandAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(offSet)
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }
}
