package com.cannontech.common.util;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Base94Test {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { 0, "!!!" },
            { 1, "!!\"" },
            { 2, "!!#" },
            { 3, "!!$" },

            { 47, "!!P" },

            { 92, "!!}" },
            { 93, "!!~" },
            { 94, "!\"!" },
            { 95, "!\"\"" },
            { 96, "!\"#" },

            { 141, "!\"P" },
            { 142, "!\"Q" },
            { 143, "!\"R" },

            { 186, "!\"}" },
            { 187, "!\"~" },
            { 188, "!#!" },
            { 189, "!#\"" },
            { 190, "!##" },
            
            { 830582, "~~}" },
            { 830583, "~~~" },
            { 830584, "\"!!!" },
            { 830585, "\"!!\"" },

            { 78074894, "~~~}" },
            { 78074895, "~~~~" },
            { 78074896, "\"!!!!" },
            { 78074897, "\"!!!\"" },

            { Short.MAX_VALUE, "$cX" },
            { Integer.MAX_VALUE, "<PP}d" },
            { Long.MAX_VALUE, "1**0#VEx9D" },
        });
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
