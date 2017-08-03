package com.cannontech.common.util.jms.api;

import static org.junit.Assert.*;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Multimap;

public class JmsApiDirectoryTest {
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_allQueuesHaveCategory() {
        Multimap<JmsApiCategory, JmsApi<?,?,?>> categoryToApi;
        try {
            //Use reflection to get the map of categories to apis
            categoryToApi = (Multimap<JmsApiCategory, JmsApi<?,?,?>>) ReflectionTestUtils.getField(JmsApiDirectory.class, "jmsApis");
        } catch (ExceptionInInitializerError e) {
            throw new IllegalStateException("Error initializing category to API map: " + e.getCause().getMessage(), e);
        }
        
        // Use reflection to get all static fields from JmsApiDirectory
        Arrays.stream(JmsApiDirectory.class.getDeclaredFields())
              .filter(field -> Modifier.isStatic(field.getModifiers()))
              // Any static fields of JmsApiDirectory that are not JmsApi definitions need to be filtered here!
              .filter(field -> !field.getName().equals("jmsApis"))
              .forEach(field -> {
                  try {
                      field.setAccessible(true);
                      JmsApi<?,?,?> api = (JmsApi<?,?,?>) field.get(null);
                      assertTrue(api + " is not assigned to a category", categoryToApi.containsValue(api));
                 } catch (Exception e) {
                     e.printStackTrace(System.out);
                     fail("Error comparing static fields to Apis assigned to categories.\n" + e.toString());
                 }
              });
        
    }
    
    //TODO: check for duplicates?
}
