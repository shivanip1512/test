package com.cannontech.loadcontrol.messages;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ConstraintError {
	
	OUTSIDE_SEASON_SCHEDULE(100) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("startTime", constraintViolation.getDatetimeParams().get(0), 
					   "scheduleID", constraintViolation.getIntegerParams().get(0));
		}
	},
	PROHIBITED_HOLIDAY_RUN(101) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("date", constraintViolation.getDatetimeParams().get(0));
		}
	},
	PROHIBITED_WEEKDAY_RUN(102) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("weekday", constraintViolation.getStringParams().get(0));
		}
	},
	EXCEEDED_DAILY_CONTROL_HOURS(103) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("groupName", constraintViolation.getStringParams().get(0),
					   "numHours",  constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_MONTHLY_CONTROL_HOURS(104) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("groupName", constraintViolation.getStringParams().get(0),
					   "numHours",  constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_SEASONAL_CONTROL_HOURS(105) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("groupName", constraintViolation.getStringParams().get(0),
					   "numHours",  constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_ANNUAL_CONTROL_HOURS(106) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("groupName", constraintViolation.getStringParams().get(0),
					   "numHours",  constraintViolation.getDoubleParams().get(0));
		}
	},
	CONTROLLED_LESS_THAN_MINIMUM(107) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("numSeconds", constraintViolation.getDoubleParams().get(0));
		}
	},
	CONTROLLED_MORE_THAN_MAXIMUM(108) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("numHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	MIN_RESTART_TIME_VIOLATION(109) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",  constraintViolation.getStringParams().get(0),
					   "numHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	MAXIMUM_DAILY_OPERATIONS_REACHED(110) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",     constraintViolation.getStringParams().get(0),
					   "maxDailyOps", constraintViolation.getIntegerParams().get(0));
		}
	},
	PROPOSED_TIMES_SPAN_MULTIPLE_WINDOWS(111) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	CANNOT_RUN_OUTSIDE_CONTROL_WINDOWS(112) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	STOP_TIME_OUTSIDE_CONTROL_WINDOW(113) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStop",   constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
				       "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	START_TIME_OUTSIDE_CONTROL_WINDOW(114) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStart",  constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
					   "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	PROPOSED_START_AFTER_STOP(115) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStart", constraintViolation.getDatetimeParams().get(0),
					   "proposedStop",  constraintViolation.getDatetimeParams().get(1));
		}
	},
	PROPOSED_STOP_BEFORE_START(116) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map(); // This one isn't used yet (or maybe ever).
		}
	},
	INVALID_PROPOSED_CA_START_TIME_SAME_DATE(117) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStart",  constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
					   "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	INVALID_PROPOSED_CA_STOP_TIME_SAME_DATE(118) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStop",   constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
					   "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	INVALID_PROPOSED_CA_START_TIME_OVER_MIDNIGHT(119) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStart",  constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
					   "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	INVALID_PROPOSED_CA_STOP_TIME_OVER_MIDNIGHT(120) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStop",   constraintViolation.getDatetimeParams().get(0),
					   "availableStart", constraintViolation.getDatetimeParams().get(1),
					   "availableStop",  constraintViolation.getDatetimeParams().get(2));
		}
	},
	PROPOSED_START_TOO_SOON(121) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("proposedStart",  constraintViolation.getDatetimeParams().get(0),
					   "minutesFromNow", constraintViolation.getDoubleParams().get(0),
					   "offsetMinutes",  constraintViolation.getDoubleParams().get(1));
		}
	},
	MASTER_PROGRAM_ACTIVE(122) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	CANNOT_EXTEND_STOP_TIME(123) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("currentStop",  constraintViolation.getDatetimeParams().get(0),
					   "proposedStop", constraintViolation.getDatetimeParams().get(1));
		}
	},
	IDENTICAL_GEAR_ID(124) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	CANNOT_CHANGE_FROM_LATCHING(125) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	GEAR_CANNOT_CHANGE_STOPPING(126) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	GEAR_CANNOT_CHANGE_INACTIVE(127) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map();
		}
	},
	EXCEEDED_DAILY_CONTROL_HOURS_MSG_2(128) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",  constraintViolation.getStringParams().get(0),
					   "maxHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_DAILY_CONTROL_HOURS_MSG_3(129) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",      constraintViolation.getStringParams().get(0),
					   "currentHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_MONTHLY_CONTROL_HOURS_MSG_2(130) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",  constraintViolation.getStringParams().get(0),
					   "maxHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_MONTHLY_CONTROL_HOURS_MSG_3(131) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", 	   constraintViolation.getStringParams().get(0),
					   "currentHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_SEASONAL_CONTROL_HOURS_MSG_2(132) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",  constraintViolation.getStringParams().get(0),
					   "maxHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_SEASONAL_CONTROL_HOURS_MSG_3(133) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", 	   constraintViolation.getStringParams().get(0),
					   "currentHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_ANNUAL_CONTROL_HOURS_MSG_2(134) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName",  constraintViolation.getStringParams().get(0),
					   "maxHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	EXCEEDED_ANNUAL_CONTROL_HOURS_MSG_3(135) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", 	   constraintViolation.getStringParams().get(0),
					   "currentHours", constraintViolation.getDoubleParams().get(0));
		}
	},
	LOAD_GROUP_DISABLED(136) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_ACTIVATE_VIOLATION(137) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MIN_ACTIVATE_VIOLATION(138) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MIN_RESTART_VIOLATION(139) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_DAILY_OPS_VIOLATION(140) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_DAILY_HOURS_VIOLATION(141) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_MONTHLY_HOURS_VIOLATION(142) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_SEASONAL_HOURS_VIOLATION(143) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_MAX_ANNUAL_HOURS_VIOLATION(144) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_CANNOT_CONTROL_IN_WINDOW(145) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_CANNOT_CONTROL_IN_WINDOW_ADJUST(146) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("areaStart", constraintViolation.getDatetimeParams().get(0),
					   "areaStop",  constraintViolation.getDatetimeParams().get(1),
					   "duration",  constraintViolation.getIntegerParams().get(0));
		}
	},
	LOAD_GROUP_CANNOT_CONTROL_IN_WINDOW_NO_ADJUST(147) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("areaStart", constraintViolation.getDatetimeParams().get(0),
					   "areaStop",  constraintViolation.getDatetimeParams().get(1),
					   "duration",  constraintViolation.getIntegerParams().get(0));
		}
	},
	LOAD_GROUP_NOT_IN_PROGRAM_CONTROL_WINDOW(148) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	LOAD_GROUP_NOT_ENOUGH_TIME_LEFT_IN_WINDOW(149) {
		Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
			return map("paoName", constraintViolation.getStringParams().get(0));
		}
	},
	PROGRAM_CONTROL_WINDOW_EXCEEDS_MIDNIGHT(150) {
            Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation) {
                    return map();
        }
    };
	
	private final int errorCode;
	
	abstract Map<String, Object> getViolationParameters(ConstraintViolation constraintViolation);
	
	private final static ImmutableMap<Integer, ConstraintError> lookupByErrorCode;
	static {
        Builder<Integer, ConstraintError> byDefinitionIdBuilder =
            ImmutableMap.builder();

        for (ConstraintError applianceType : values()) {
            byDefinitionIdBuilder.put(applianceType.getErrorCode(), applianceType);
        }
        lookupByErrorCode = byDefinitionIdBuilder.build();
    }
	
	private ConstraintError(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public static ConstraintError getByErrorCode(Integer errorCode) {
        ConstraintError error = lookupByErrorCode.get(errorCode);
        return error;
    }
	
	// Functions to create the immutable map objects for the resolvable template.
	public static Map<String, Object> map() {
		return ImmutableMap.of();
	}
	public static Map<String, Object> map(String k1, Object v1) {
		return ImmutableMap.of(k1, v1);
	}
	public static Map<String, Object> map(String k1, Object v1, String k2, Object v2) {
		return ImmutableMap.of(k1, v1, k2, v2);
	}
	public static Map<String, Object> map(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
		return ImmutableMap.of(k1, v1, k2, v2, k3, v3);
	}
}