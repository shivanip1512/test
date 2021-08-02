package com.cannontech.api.error.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ApiErrorDetailsTest {

    @Test
    public void findDuplicateCode() {
        List<ApiErrorDetails> enumValues = Arrays.asList(ApiErrorDetails.values());
        List<Integer> codes = enumValues.stream()
                .map(e -> e.getCode())
                .collect(Collectors.toList());
        int duplicateCount = codes.stream()
                .distinct().collect(Collectors.toList()).size();
        assertTrue(codes.size() == duplicateCount, "Duplicate code in enum");

    }

}
