package com.eaton.builders.tools.points;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class PoinCreateService {
	
	public static Pair<JSONObject, JSONObject> buildAndCreateAnalogPointOnlyRequiredFields(Integer paoId) {
        return new AnalogPointCreateBuilder.Builder(Optional.empty())
        		.withPointOffset(Optional.empty())
        		.withPaoId(paoId)
				.withUomId(Optional.empty())
				.create();
    }
	
	public static Pair<JSONObject, JSONObject> buildAndCreateStatusPointOnlyRequiredFields(Integer paoId) {
		return new StatusPointCreateBuilder.Builder(Optional.empty())
				.withPointOffset(Optional.empty())
				.withPaoId(paoId)
				.withStateGroupId(Optional.empty())
				.withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
				.create();
	}
	
	public static Pair<JSONObject, JSONObject> buildAndCreateCalcAnalogPointOnlyRequiredFields(Integer paoId) {
		return new CalcAnalogPointCreateBuilder.Builder(Optional.empty())
				.withPointOffset(Optional.empty())
				.withPaoId(paoId)
				.create();
	}
	
	public static Pair<JSONObject, JSONObject> buildAndCreateCalcStatusPointOnlyRequiredFields(Integer paoId) {
		return new CalcStatusPointCreateBuilder.Builder(Optional.empty())
				.withPointOffset(Optional.empty())
				.withPaoId(paoId)
				.withStateGroupId(Optional.empty())
				.withArchiveType(Optional.of(PointEnums.ArchiveType.getRandomArchiveType()))
				.create();
	}
	
	public static Pair<JSONObject, JSONObject> buildAndCreatePulseAccumulatorPointOnlyRequiredFields(Integer paoId) {
        return new PulseAccumulatorPointCreateBuilder.Builder(Optional.empty())
        		.withPointOffset(Optional.empty())
        		.withPaoId(paoId)
				.withUomId(Optional.empty())
				.create();
    }
	
	public static Pair<JSONObject, JSONObject> buildAndCreateDemandAccumulatorPointOnlyRequiredFields(Integer paoId) {
        return new DemandAccumulatorPointCreateBuilder.Builder(Optional.empty())
        		.withPointOffset(Optional.empty())
        		.withPaoId(paoId)
				.withUomId(Optional.empty())
				.create();
    }
}
