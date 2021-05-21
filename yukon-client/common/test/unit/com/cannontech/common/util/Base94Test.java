package com.cannontech.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

public class Base94Test {

    private static ClassPathResource testCases = new ClassPathResource("testCases/base94.txt");
    
    public static Collection<Object[]> data() throws IOException {
        
        var lineReader = new BufferedReader(new InputStreamReader(testCases.getInputStream()));
        
        return lineReader.lines()
                .filter(StringUtils::isNotEmpty)
                .filter(s -> !s.contains("cpp-delimiter"))  //  filter out the required C++ delimiters at the start and end of the file
                .map(StringUtils::split)
                .map(testCase -> new Object[] { Long.valueOf(testCase[0]), testCase[1] })
                .collect(Collectors.toList());
    }
    
    private long input;
    private String expected;
    
    @ParameterizedTest
    @MethodSource("data")
    public void test(ArgumentsAccessor argumentsAccessor) {
        input = argumentsAccessor.getLong(0);
        expected = argumentsAccessor.getString(1);
        assertEquals(Base94.of(input), expected, "base94 of " + input);
    }
}
