package com.cannontech.common.events.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import com.cannontech.common.events.YukonEventLog;

public class EventLogLocalizationTest {

    private static final String eventsXmlResourcePath = "com/cannontech/yukon/common/events.xml";
    private static final String packageSearchPath = "classpath*:com/cannontech/common/events/loggers/*.class";

    private static final Set<String> knownAbsentEntries = Set.of(
            "yukon.common.events.dr.itron.addMacAddressToGroup",
            "yukon.common.events.endpoint.device.changeCancelled",
            "yukon.common.events.endpoint.device.changeTypeCancelled",
            "yukon.common.events.endpoint.device.deleteCancelled",
            "yukon.common.events.endpoint.point.pointsCreateCancelled",
            "yukon.common.events.endpoint.point.pointsDeleteCancelled",
            "yukon.common.events.endpoint.point.pointsUpdateCancelled",
            "yukon.common.events.multispeak.removeDevice");

    @Test
    public void testLocalizationEntry() throws InvalidPropertiesFormatException, IOException, ClassNotFoundException {
        var resourcePatternResolver = new PathMatchingResourcePatternResolver();
        var metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        var resources = resourcePatternResolver.getResources(packageSearchPath);

        assertFalse("No resources", resources.length == 0);

        var classes = new ArrayList<Class<?>>();
        for (var resource : resources) {
            assertTrue(resource.isReadable());
            // Do this outside the stream so it can throw
            var className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            classes.add(Class.forName(className));
        }

        assertFalse("No classes found", classes.isEmpty());

        var eventsXml = getClass().getClassLoader().getResourceAsStream(eventsXmlResourcePath);
        var localizationEntries = new Properties();
        localizationEntries.loadFromXML(eventsXml);

        var eventLogMethods = classes.stream()
                .flatMap(c -> Arrays.stream(c.getMethods()))
                .filter(m -> m.isAnnotationPresent(YukonEventLog.class))
                .map(m -> "yukon.common.events." + m.getAnnotation(YukonEventLog.class).category() + "." + m.getName())
                .collect(Collectors.toList());

        assertFalse("No event log methods found", eventLogMethods.isEmpty());

        var missing = eventLogMethods.stream()
                .filter(Predicate.not(localizationEntries::containsKey))
                .filter(Predicate.not(knownAbsentEntries::contains))
                .sorted()
                .collect(Collectors.toList());

        assertTrue("Event log methods missing localization in events.xml: (" + missing.size() + " total)\n"
                + String.join("\n", missing), missing.isEmpty());
    }
}
