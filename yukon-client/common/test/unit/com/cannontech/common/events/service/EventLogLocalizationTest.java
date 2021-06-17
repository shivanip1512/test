package com.cannontech.common.events.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

import com.cannontech.common.events.YukonEventLog;

public class EventLogLocalizationTest {

    private static final String eventsXmlResourcePath = "com/cannontech/yukon/common/events.xml";
    private static final String packageSearchPath = "classpath*:com/cannontech/common/events/loggers/*.class";

    @Test
    public void testLocalizationEntry() throws InvalidPropertiesFormatException, IOException, ClassNotFoundException {
        var resourcePatternResolver = new PathMatchingResourcePatternResolver();
        var metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        var resources = resourcePatternResolver.getResources(packageSearchPath);

        assertFalse(resources.length == 0, "No resources");

        var classes = new ArrayList<Class<?>>();
        for (var resource : resources) {
            assertTrue(resource.isReadable());
            // Do this outside the stream so it can throw
            var className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            classes.add(Class.forName(className));
        }

        assertFalse(classes.isEmpty(), "No classes found");

        var eventsXml = getClass().getClassLoader().getResourceAsStream(eventsXmlResourcePath);
        var localizationEntries = new Properties();
        localizationEntries.loadFromXML(eventsXml);

        var eventLogMethods = classes.stream()
                .flatMap(c -> Arrays.stream(c.getMethods()))
                .filter(m -> m.isAnnotationPresent(YukonEventLog.class))
                .map(m -> "yukon.common.events." + m.getAnnotation(YukonEventLog.class).category() + "." + m.getName())
                .collect(Collectors.toList());

        assertFalse(eventLogMethods.isEmpty(), "No event log methods found");

        var missing = eventLogMethods.stream()
                .filter(Predicate.not(localizationEntries::containsKey))
                .sorted()
                .collect(Collectors.toList());

        assertTrue(missing.isEmpty(), "Event log methods missing localization in events.xml: (" + missing.size() + " total)\n"
                + String.join("\n", missing));
    }
}
