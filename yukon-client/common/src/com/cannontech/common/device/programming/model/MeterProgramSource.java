package com.cannontech.common.device.programming.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MeterProgramSource implements DisplayableEnum {
	YUKON("Y"), 
	OPTICAL("P"), 
	NEW("N"), 
	UNPROGRAMMED("U")
	;
	
	private static Map<String, MeterProgramSource> prefixToSource = Arrays.stream(MeterProgramSource.values())
			.collect(Collectors.toMap(MeterProgramSource::getPrefix, Function.identity()));
	
	private String prefix;

	private MeterProgramSource(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static MeterProgramSource getByPrefix(String prefix) {
		return prefixToSource.get(prefix);
	}
	
	public boolean isYukon() {
		return this == YUKON;
	}

	public boolean isNotYukon() {
		return !isYukon();
	}

    @Override
    public String getFormatKey() {
        return null;
    }
}
