package com.cannontech.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.io.ClassPathResource;

@RunWith(Parameterized.class)
public class Base94Test {

    private static ClassPathResource testCases = new ClassPathResource("testCases/base94.txt");
    
    @Parameters
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
    
    public Base94Test(long input, String expected) {
        this.input = input;
        this.expected = expected;
    }
    
    @Test
    public void test() {
        Assert.assertEquals("base94 of " + input, Base94.of(input), expected);
    }        
}
