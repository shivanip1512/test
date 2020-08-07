package com.cannontech.web.spring.reslovers.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.web.spring.PagingParametersHandlerMethodArgumentResolver;
import com.cannontech.web.spring.parameters.exceptions.InvalidPagingParametersException;

public class PagingParametersHandlerMethodArgumentResolverTest {

    private static final PagingParametersHandlerMethodArgumentResolver pagingResolver = new PagingParametersHandlerMethodArgumentResolver();

    // Tests for getValidItemsPerPage
    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidItemsPerPageForZeroValue() {
        String itemsPerPage = "0";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
    }

    @Test
    public void testGetValidItemsPerPageForNullValue() {
        String itemsPerPage = null;
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        assertTrue("Is Valid Items Per Page ", result.equals(25));
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidItemsPerPageForNegativeValue() {
        String itemsPerPage = "-1";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidItemsPerPageForBigIntegerValue() {
        String itemsPerPage = "100000000000000000000000000";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidItemsPerPageForMoreThanThousandValue() {
        String itemsPerPage = "1005";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
    }

    @Test
    public void testGetValidItemsPerPageForValidValue() {
        String itemsPerPage = "250";
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        assertTrue("Is Valid Items Per Page ", result.equals(250));
    }

    // Tests for getValidPageNumber
    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidPageNumberForZeroValue() {
        String itemsPerPage = "0";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidPageNumberForNullValue() {
        String itemsPerPage = null;
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidPageNumberForNegativeValue() {
        String itemsPerPage = "-1";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    @Test(expected = InvalidPagingParametersException.class)
    public void testGetValidPageNumberForBigIntegerValue() {
        String itemsPerPage = "100000000000000000000000000";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    public void testGetValidPageNumberForValidValue() {
        String itemsPerPage = "250";
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
        assertTrue("Is Valid Items Per Page ", result.equals(250));
    }

}
