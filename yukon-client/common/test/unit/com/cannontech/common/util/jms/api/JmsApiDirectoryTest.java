package com.cannontech.common.util.jms.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class JmsApiDirectoryTest {
    
    @Test
    public void test_allQueuesHaveCategory() {
        List<JmsApi<?,?,?>> apis;
        try {
            //Get the list of apis 
            Map<JmsApiCategory, List<JmsApi<?,?,?>>> categoryToApi = JmsApiDirectory.getQueueDescriptions();
            apis = categoryToApi.values()
                                .stream()
                                .flatMap(List::stream)
                                .collect(Collectors.toList());
        } catch (ExceptionInInitializerError e) {
            throw new IllegalStateException("Error initializing category to API map: " + e.getCause().getMessage(), e);
        }
        
        // Use reflection to get all static fields from JmsApiDirectory
        Arrays.stream(JmsApiDirectory.class.getDeclaredFields())
              .filter(field -> Modifier.isStatic(field.getModifiers()))
              // Any static fields of JmsApiDirectory that are not JmsApi definitions need to be filtered here!
              .filter(field -> !field.getName().equals("API_COMPARATOR"))
              .forEach(field -> {
                 try {
                      field.setAccessible(true);
                      JmsApi<?,?,?> api = (JmsApi<?,?,?>) field.get(null);
                      assertTrue(apis.contains(api), api + " is not assigned to a category");
                 } catch (Exception e) {
                     e.printStackTrace(System.out);
                     fail("Error comparing static fields to Apis assigned to categories.\n" + e.toString());
                 }
              });
    }
}
