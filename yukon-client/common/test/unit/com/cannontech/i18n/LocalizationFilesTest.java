package com.cannontech.i18n;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@RunWith(Parameterized.class)
public class LocalizationFilesTest {

    @Parameter(0) public Resource resource;

    private static Map<String, Set<String>> knownIdenticalDuplicatesPerFile = getKnownIdenticalDuplicates();
    private static Map<String, Set<String>> knownDifferingDuplicatesPerFile = getKnownDifferingDuplicates();
    private static String pathPrefix = "com/cannontech/yukon/";

    @Parameters(name = "{0}")
    public static Object[] getResources() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources("classpath*:" + pathPrefix + "**/*.xml");
    }

    @Test
    public void testDuplicateEntries() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        var document = builder.build(resource.getInputStream());

        var rootElement = document.getRootElement();
        
        Optional<String> relativeFilename = Optional.of(resource.getURI().toString())
                .filter(f -> f.contains(pathPrefix))
                .map(f -> f.substring(f.indexOf(pathPrefix) + pathPrefix.length()));
        
        Set<String> knownIdenticalDuplicates = relativeFilename
                .map(knownIdenticalDuplicatesPerFile::get)
                .orElseGet(Collections::emptySet);

        Set<String> knownDifferingDuplicates = relativeFilename
                .map(knownDifferingDuplicatesPerFile::get)
                .orElseGet(Collections::emptySet);
        
        //  Only check properties files
        if (rootElement.getName().equals("properties")) {
            //  Map of property to map of count of values
            Map<Object, Map<String, Long>> keyEntries = 
                    rootElement.getChildren("entry").stream()
                        .collect(Collectors.groupingBy(e -> e.getAttribute("key").getValue(), 
                                 Collectors.groupingBy(Element::getValue, 
                                 Collectors.counting())));

            var differingDuplicates = Maps.filterValues(keyEntries,
                    valueCounts -> valueCounts.size() > 1);

            var unexpectedDiffering = Maps.filterKeys(differingDuplicates,
                    key -> !knownDifferingDuplicates.contains(key));
            
            var missingDiffering = Sets.filter(knownDifferingDuplicates,
                    key -> !differingDuplicates.containsKey(key));
            
            assertTrue("Found duplicates with differing values: " + unexpectedDiffering, unexpectedDiffering.isEmpty());
            assertTrue("Entries listed as differing duplicates, but not found to be: " + missingDiffering, missingDiffering.isEmpty());
            
            var identicalDuplicates = Maps.filterValues(keyEntries,
                    valueCounts -> valueCounts.values().stream().anyMatch(size -> size > 1));

            var unexpectedIdentical = Maps.filterKeys(identicalDuplicates,
                    key -> !knownIdenticalDuplicates.contains(key));
            
            var missingIdentical = Sets.filter(knownIdenticalDuplicates,
                    key -> !identicalDuplicates.containsKey(key));
            
            assertTrue("Found duplicates with identical values:" + unexpectedIdentical.keySet(), unexpectedIdentical.isEmpty());
            assertTrue("Entries listed as identical duplicates, but not found to be: " + missingIdentical, missingIdentical.isEmpty());
        }
    }

    // TODO - resolve these duplicates
    //  Since the values for these duplicated keys are identical, the duplicates can be removed without analyzing which entry is correct.
    private static Map<String, Set<String>> getKnownIdenticalDuplicates() {
        return ImmutableMap.<String, Set<String>>builder()
                .put("common/device/archiveAnalysis.xml", Set.of(
                        "yukon.web.modules.tools.bulk.archivedValueExporter.padding",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.field",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.fieldSize",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.plainTextInput",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.attribute",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.endDate",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.roundingMode",
                        "yukon.web.modules.tools.bulk.archivedValueExporter.character"))
                .put("web/deviceGroups.xml", Set.of(
                        "yukon.web.deviceGroups.editor.operationsContainer.cannotEditGroupText"))
                .put("web/modules/adminSetup/userGroupEditor.xml", Set.of(
                        "yukon.web.modules.adminSetup.auth.role.group.description"))
                .put("web/modules/amr/root.xml", Set.of(
                        "yukon.web.modules.amr.firstName",
                        "yukon.web.modules.amr.phoneNumbers",
                        "yukon.web.modules.amr.middleName",
                        "yukon.web.modules.amr.emailAddresses",
                        "yukon.web.modules.amr.options.title"))
                .put("web/modules/capcontrol/root.xml", Set.of(
                        "yukon.web.modules.capcontrol.actions",
                        "yukon.web.modules.capcontrol.substations",
                        "yukon.web.modules.capcontrol.scheduleAssignments.noDeviceSelected",
                        "yukon.web.modules.capcontrol.scheduleAssignments.allSchedules",
                        "yukon.web.modules.capcontrol.scheduleAssignments.allCommands"))
                .put("web/modules/dr/demandResponse.xml", Set.of(
                        "yukon.dr.config.utility"))
                .put("web/modules/dr/setup.xml", Set.of(
                        "yukon.web.modules.dr.setup.gear.rampIn.title",
                        "yukon.web.modules.dr.setup.gear.whenToChange",
                        "yukon.web.modules.dr.setup.gear.cyclePeriod",
                        "yukon.web.modules.dr.setup.gear.howToStopControl",
                        "yukon.web.modules.dr.setup.gear.triggerNumber",
                        "yukon.web.modules.dr.setup.gear.cycleCountSendType",
                        "yukon.web.modules.dr.setup.gear.general.title",
                        "yukon.web.modules.dr.setup.gear.criticality",
                        "yukon.web.modules.dr.setup.gear.rampIn",
                        "yukon.web.modules.dr.setup.gear.title",
                        "yukon.web.modules.dr.setup.gear.controlParameters.title",
                        "yukon.web.modules.dr.setup.gear.maxCycleCount",
                        "yukon.web.modules.dr.setup.gear.changePrority",
                        "yukon.web.modules.dr.setup.gear.groupSelectionMethod",
                        "yukon.web.modules.dr.setup.gear.noRamp",
                        "yukon.web.modules.dr.setup.gear.gearName",
                        "yukon.web.modules.dr.setup.gear.startingPeriodCount",
                        "yukon.web.modules.dr.setup.gear.controlPercent",
                        "yukon.web.modules.dr.setup.gear.optionalAttributes.title",
                        "yukon.web.modules.dr.setup.gear.gearType",
                        "yukon.web.modules.dr.setup.gear.stopCommandRepeat",
                        "yukon.web.modules.dr.setup.gear.whenToChange.title",
                        "yukon.web.modules.dr.setup.loadGroup.addressUsage",
                        "yukon.web.modules.dr.setup.gear.stopControl.title",
                        "yukon.web.modules.dr.setup.gear.triggerOffset",
                        "yukon.web.modules.dr.setup.gear.kWReduction",
                        "yukon.web.modules.dr.setup.loadGroup.virtualRelayId",
                        "yukon.web.modules.dr.setup.gear.commandResendRate",
                        "yukon.web.modules.dr.setup.gear.rampOut"))
                .put("web/modules/operator/hardware.xml", Set.of(
                        "yukon.web.modules.operator.hardware.actions"))
                .put("web/modules/stars/survey.xml", Set.of(
                        "yukon.web.modules.adminSetup.survey.report.startDate",
                        "yukon.web.modules.adminSetup.survey.report.endDate"))
                .put("web/modules/support/root.xml", Set.of(
                        "yukon.web.modules.support.supportBundle.includeLbl"))
                .put("web/modules/tools/collectionActions.xml", Set.of(
                        "yukon.web.modules.tools.collectionActions.recentResults.userName"))
                .put("web/modules/tools/dataStreaming.xml", Set.of(
                        "yukon.web.modules.tools.dataStreaming.discrepancies.status"))
                .put("web/modules/tools/scripts.xml", Set.of(
                        "yukon.web.modules.tools.schedule.type",
                        "yukon.web.modules.tools.schedule.scriptOptions.filePath"))
                .put("web/modules/visualDisplays/visualDisplays.xml", Set.of(
                        "yukon.web.modules.dr.hrEndLabel",
                        "yukon.web.modules.dr.hrLabel",
                        "yukon.web.modules.dr.hourlyDataTypeEnum.TODAY_INTEGRATED_HOURLY_DATA"))
                .build();
    }

    // TODO - resolve these duplicates
    //  Since the values for these duplicated keys differ, some effort needs to be used to determine which entry is correct.
    //    In practice, the last entry in the file wins, so the last duplicate is what Yukon is currently displaying. 
    private static Map<String, Set<String>> getKnownDifferingDuplicates() {
        return ImmutableMap.<String, Set<String>>builder()
                .put("web/components.xml", Set.of(
                        "yukon.web.components.image.remove.hoverText"))
                .put("web/exception.xml", Set.of(
                        "yukon.exception.processingException.deviceNotFound",
                        "yukon.exception.processingException.invalidDeviceType"))
                .put("web/modules/adminSetup/root.xml", Set.of(
                        "yukon.web.modules.adminSetup.substationToRouteMapping.noRoutes"))
                .put("web/modules/amr/meterEventsReport.xml", Set.of(
                        "yukon.web.modules.amr.meterEventsReport.report.scheduleButton"))
                .put("web/modules/capcontrol/root.xml", Set.of(
                        "yukon.web.modules.capcontrol.dmvTest.deleteFailed"))
                .put("web/modules/dev/root.xml", Set.of(
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.option.roleProperties.title",
                        "yukon.web.modules.dev.setupDatabase.setupDevDatabase.option.roleProperties"))
                .put("web/modules/dr/demandResponse.xml", Set.of(
                        "yukon.web.modules.dr.loadGroupDetail.parents.noPermission"))
                .put("web/modules/dr/setup.xml", Set.of(
                        "yukon.web.modules.dr.setup.controlArea.trigger.title",
                        "yukon.web.modules.dr.setup.loadGroup.error.invalidDeviceType",
                        "yukon.web.modules.dr.setup.gear.trueCycle"))
                .put("web/modules/operator/workOrder.xml", Set.of(
                        "yukon.web.modules.operator.workOrder.currentState"))
                .put("web/modules/tools/root.xml", Set.of(
                        "yukon.web.modules.tools.tdc.altScan.title"))
                .put("web/modules/tools/scripts.xml", Set.of(
                        "yukon.web.modules.tools.schedule.scriptOptions.fileName"))
                .build();
    }
}
