package com.cannontech.web.picker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

public class PickerTest {

    // This test case is to find any missing picker entries as @JsonSubTypes in Picker.class
    @Test
    public void findMissingPickerEntries() {
        List<Class> classInPicker = new ArrayList<>();
        JsonSubTypes subtypes = Picker.class.getAnnotation(JsonSubTypes.class);
        Type[] types = subtypes.value();

        for (int type = 0; type < types.length; type++) {
            Type t = types[type];
            classInPicker.add(t.value());
        }

        // Get classes which extends Picker
        List<Class> classExtendsPicker = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Picker.class));

        // Scan picker package
        Set<BeanDefinition> components = provider.findCandidateComponents("com/cannontech/web");
        for (BeanDefinition component : components) {
            try {
                classExtendsPicker.add(Class.forName(component.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                // This test case will break if expected class is not found.
                assertTrue("Class Not Found ", 1 == 2);
            }
        }
        // These are added as they are abstract class and are not picked by above scanner.
        // Adding these here as I think at some point we will be removing these entries from Picker.java.
        classExtendsPicker.add(com.cannontech.web.picker.BasePicker.class);
        classExtendsPicker.add(com.cannontech.web.picker.DatabasePicker.class);
        classExtendsPicker.add(com.cannontech.web.picker.LucenePicker.class);
        classExtendsPicker.add(com.cannontech.web.picker.DatabasePaoPicker.class);

        classExtendsPicker.removeAll(classInPicker);
        assertEquals("Picker not defined as @JsonSubTypes in Picker.class", classExtendsPicker, new ArrayList<Class>());
    }
}
