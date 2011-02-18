package com.cannontech.common.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

public class MatchStyleTest {
    
    private Set<Integer> A = Sets.newHashSet(1);
    private Set<Integer> B = Sets.newHashSet(2);
    private Set<Integer> AB = Sets.newHashSet(1,2);
    private Set<Integer> EMPTY = Sets.newHashSet();
    
    // none
    @Test
    public void test_none() {
        Assert.assertEquals(false, MatchStyle.none.matches(A, A));
        Assert.assertEquals(false, MatchStyle.none.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.none.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.none.matches(A, B));
        Assert.assertEquals(true, MatchStyle.none.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.none.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.none.matches(EMPTY, EMPTY));
    }
    // some
    @Test
    public void test_some() {
        Assert.assertEquals(true, MatchStyle.some.matches(A, A));
        Assert.assertEquals(true, MatchStyle.some.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.some.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.some.matches(A, B));
        Assert.assertEquals(false, MatchStyle.some.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.some.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.some.matches(EMPTY, EMPTY));
    }
    // any
    @Test
    public void test_any() {
        Assert.assertEquals(true, MatchStyle.any.matches(A, A));
        Assert.assertEquals(true, MatchStyle.any.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.any.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.any.matches(A, B));
        Assert.assertEquals(false, MatchStyle.any.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.any.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.any.matches(EMPTY, EMPTY));
    }
    // notany
    @Test
    public void test_notany() {
        Assert.assertEquals(false, MatchStyle.notany.matches(A, A));
        Assert.assertEquals(false, MatchStyle.notany.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.notany.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.notany.matches(A, B));
        Assert.assertEquals(true, MatchStyle.notany.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.notany.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.notany.matches(EMPTY, EMPTY));
    }
    // all
    @Test
    public void test_all() {
        Assert.assertEquals(true, MatchStyle.all.matches(A, A));
        Assert.assertEquals(true, MatchStyle.all.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.all.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.all.matches(A, B));
        Assert.assertEquals(false, MatchStyle.all.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.all.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.all.matches(EMPTY, EMPTY));
    }
    // notall
    @Test
    public void test_notall() {
        Assert.assertEquals(false, MatchStyle.notall.matches(A, A));
        Assert.assertEquals(false, MatchStyle.notall.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.notall.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.notall.matches(A, B));
        Assert.assertEquals(true, MatchStyle.notall.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.notall.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.notall.matches(EMPTY, EMPTY));
    }
    // subset
    @Test
    public void test_subset() {
        Assert.assertEquals(true, MatchStyle.subset.matches(A, A));
        Assert.assertEquals(false, MatchStyle.subset.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.subset.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.subset.matches(A, B));
        Assert.assertEquals(true, MatchStyle.subset.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.subset.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.subset.matches(EMPTY, EMPTY));
    }
    // notsubset
    @Test
    public void test_notsubset() {
        Assert.assertEquals(false, MatchStyle.notsubset.matches(A, A));
        Assert.assertEquals(true, MatchStyle.notsubset.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.notsubset.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.notsubset.matches(A, B));
        Assert.assertEquals(false, MatchStyle.notsubset.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.notsubset.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.notsubset.matches(EMPTY, EMPTY));
    }
    // equal
    @Test
    public void test_equal() {
        Assert.assertEquals(true, MatchStyle.equal.matches(A, A));
        Assert.assertEquals(false, MatchStyle.equal.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.equal.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.equal.matches(A, B));
        Assert.assertEquals(false, MatchStyle.equal.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.equal.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.equal.matches(EMPTY, EMPTY));
    }
    // notequal
    @Test
    public void test_notequal() {
        Assert.assertEquals(false, MatchStyle.notequal.matches(A, A));
        Assert.assertEquals(true, MatchStyle.notequal.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.notequal.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.notequal.matches(A, B));
        Assert.assertEquals(true, MatchStyle.notequal.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.notequal.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.notequal.matches(EMPTY, EMPTY));
    }
    // empty
    @Test
    public void test_empty() {
        Assert.assertEquals(false, MatchStyle.empty.matches(A, A));
        Assert.assertEquals(false, MatchStyle.empty.matches(AB, A));
        Assert.assertEquals(false, MatchStyle.empty.matches(A, AB));
        Assert.assertEquals(false, MatchStyle.empty.matches(A, B));
        Assert.assertEquals(true, MatchStyle.empty.matches(EMPTY, A));
        Assert.assertEquals(false, MatchStyle.empty.matches(A, EMPTY));
        Assert.assertEquals(true, MatchStyle.empty.matches(EMPTY, EMPTY));
    }
    // notempty
    @Test
    public void test_notempty() {
        Assert.assertEquals(true, MatchStyle.notempty.matches(A, A));
        Assert.assertEquals(true, MatchStyle.notempty.matches(AB, A));
        Assert.assertEquals(true, MatchStyle.notempty.matches(A, AB));
        Assert.assertEquals(true, MatchStyle.notempty.matches(A, B));
        Assert.assertEquals(false, MatchStyle.notempty.matches(EMPTY, A));
        Assert.assertEquals(true, MatchStyle.notempty.matches(A, EMPTY));
        Assert.assertEquals(false, MatchStyle.notempty.matches(EMPTY, EMPTY));
    }
}