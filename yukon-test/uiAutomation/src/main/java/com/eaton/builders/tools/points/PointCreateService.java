package com.eaton.builders.tools.points;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class PointCreateService {

    public static Pair<JSONObject, JSONObject> buildAndCreateAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new AnalogPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new StatusPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateCalcAnalogPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new CalcAnalogPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateCalcStatusPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new CalcStatusPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .withStateGroupId(Optional.empty())
                .withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreatePulseAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new PulseAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }

    public static Pair<JSONObject, JSONObject> buildAndCreateDemandAccumulatorPointOnlyRequiredFields(Integer paoId, Optional<String> name) {
        return new DemandAccumulatorPointCreateBuilder.Builder(name)
                .withPointOffset(Optional.empty())
                .withPaoId(paoId)
                .withUomId(Optional.empty())
                .create();
    }
}
