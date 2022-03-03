package com.cannontech.web.spring.reslovers.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.web.spring.PagingParametersHandlerMethodArgumentResolver;
import com.cannontech.web.spring.parameters.exceptions.InvalidPagingParametersException;

public class PagingParametersHandlerMethodArgumentResolverTest {

    private static final PagingParametersHandlerMethodArgumentResolver pagingResolver = new PagingParametersHandlerMethodArgumentResolver();

    // Tests for getValidItemsPerPage
    @Test
    public void testGetValidItemsPerPageForZeroValue() {
        String itemsPerPage = "0";
        Assertions.assertThrows(InvalidPagingParametersException.class, () -> {
            ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        });
    }

    @Test
    public void testGetValidItemsPerPageForNullValue() {
        String itemsPerPage = null;
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        assertTrue(result.equals(25), "Is Valid Items Per Page ");
    }

    @Test
    public void testGetValidItemsPerPageForNegativeValue() {
        String itemsPerPage = "-1";
        Assertions.assertThrows(InvalidPagingParametersException.class, () -> {
            ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        });
    }

    @Test
    public void testGetValidItemsPerPageForBigIntegerValue() {
        String itemsPerPage = "100000000000000000000000000";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
    }

    @Test
    public void testGetValidItemsPerPageForMoreThanThousandValue() {
        String itemsPerPage = "1005";
        Assertions.assertThrows(InvalidPagingParametersException.class, () -> {
            ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        });
    }

    @Test
    public void testGetValidItemsPerPageForValidValue() {
        String itemsPerPage = "250";
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidItemsPerPage", itemsPerPage);
        assertTrue(result.equals(250), "Is Valid Items Per Page ");
    }

    // Tests for getValidPageNumber
    @Test
    public void testGetValidPageNumberForZeroValue() {
        String itemsPerPage = "0";
        Assertions.assertThrows(InvalidPagingParametersException.class, () -> {
            ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
        });
    }

    @Test
    public void testGetValidPageNumberForNullValue() {
        String itemsPerPage = null;
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    @Test
    public void testGetValidPageNumberForNegativeValue() {
        String itemsPerPage = "-1";
        Assertions.assertThrows(InvalidPagingParametersException.class, () -> {
            ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
        });
    }

    @Test
    public void testGetValidPageNumberForBigIntegerValue() {
        String itemsPerPage = "100000000000000000000000000";
        ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
    }

    public void testGetValidPageNumberForValidValue() {
        String itemsPerPage = "250";
        var result = ReflectionTestUtils.invokeMethod(pagingResolver, "getValidPageNumber", itemsPerPage);
        assertTrue(result.equals(250), "Is Valid Items Per Page ");
    }

}
