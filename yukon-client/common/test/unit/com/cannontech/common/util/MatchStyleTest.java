package com.cannontech.common.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static com.cannontech.common.util.MatchStyle.*;
import com.google.common.collect.Sets;

public class MatchStyleTest {
    
    private Set<Integer> A = Sets.newHashSet(1);
    private Set<Integer> B = Sets.newHashSet(2);
    private Set<Integer> AB = Sets.newHashSet(1,2);
    private Set<Integer> E = Sets.newHashSet();
    
    private void validate(Set<Integer> one, MatchStyle style, Set<Integer> two, boolean expectedResult) {
        Assert.assertEquals(expectedResult, style.matches(one, two));
    }

    @Test
    public void test() {
        validate(A, none, A, false);
        validate(AB,none, A, false);
        validate(A, none, AB,false);
        validate(A, none, B, true);
        validate(E, none, A, true);
        validate(A, none, E, true);
        validate(E, none, E, true);
        
        validate(A, some, A, true);
        validate(AB,some, A, true);
        validate(A, some, AB,true);
        validate(A, some, B, false);
        validate(E, some, A, false);
        validate(A, some, E, false);
        validate(E, some, E, false);
        
        validate(A, any, A, true);
        validate(AB,any, A, true);
        validate(A, any, AB,true);
        validate(A, any, B, false);
        validate(E, any, A, false);
        validate(A, any, E, true);
        validate(E, any, E, true);
        
        validate(A, notany, A, false);
        validate(AB,notany, A, false);
        validate(A, notany, AB,false);
        validate(A, notany, B, true);
        validate(E, notany, A, true);
        validate(A, notany, E, false);
        validate(E, notany, E, false);
        
        validate(A, all, A, true);
        validate(AB,all, A, true);
        validate(A, all, AB,false);
        validate(A, all, B, false);
        validate(E, all, A, false);
        validate(A, all, E, true);
        validate(E, all, E, true);
        
        validate(A, notall, A, false);
        validate(AB,notall, A, false);
        validate(A, notall, AB,true);
        validate(A, notall, B, true);
        validate(E, notall, A, true);
        validate(A, notall, E, false);
        validate(E, notall, E, false);
        
        validate(A, subset, A, true);
        validate(AB,subset, A, false);
        validate(A, subset, AB,true);
        validate(A, subset, B, false);
        validate(E, subset, A, true);
        validate(A, subset, E, false);
        validate(E, subset, E, true);
        
        validate(A, notsubset, A, false);
        validate(AB,notsubset, A, true);
        validate(A, notsubset, AB,false);
        validate(A, notsubset, B, true);
        validate(E, notsubset, A, false);
        validate(A, notsubset, E, true);
        validate(E, notsubset, E, false);
        
        validate(A, equal, A, true);
        validate(AB,equal, A, false);
        validate(A, equal, AB,false);
        validate(A, equal, B, false);
        validate(E, equal, A, false);
        validate(A, equal, E, false);
        validate(E, equal, E, true);
        
        validate(A, notequal, A, false);
        validate(AB,notequal, A, true);
        validate(A, notequal, AB,true);
        validate(A, notequal, B, true);
        validate(E, notequal, A, true);
        validate(A, notequal, E, true);
        validate(E, notequal, E, false);
        
        validate(A, empty, A, false);
        validate(AB,empty, A, false);
        validate(A, empty, AB,false);
        validate(A, empty, B, false);
        validate(E, empty, A, true);
        validate(A, empty, E, false);
        validate(E, empty, E, true);
        
        validate(A, notempty, A, true);
        validate(AB,notempty, A, true);
        validate(A, notempty, AB,true);
        validate(A, notempty, B, true);
        validate(E, notempty, A, false);
        validate(A, notempty, E, true);
        validate(E, notempty, E, false);
    }
}