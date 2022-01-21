package com.cannontech.cbc.cyme.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.cbc.cyme.model.PhaseInformation;
import com.cannontech.common.model.Phase;
import com.cannontech.common.util.xml.SimpleXPathTemplate;

public class CymeSimulationHelperTest {

    @Test
    public void test_getPhaseInformation() {
        CymeSimulationHelper helper = new CymeSimulationHelper();
        SimpleXPathTemplate template = EasyMock.createMock(SimpleXPathTemplate.class);

        EasyMock.expect(template.evaluateAsString("I" + Phase.A)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("VBase" + Phase.A)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("KVAR" + Phase.A)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("KW" + Phase.A)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("RegTap" + Phase.A)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("RegVset" + Phase.A)).andStubReturn("");

        EasyMock.expect(template.evaluateAsString("I" + Phase.B)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("VBase" + Phase.B)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("KVAR" + Phase.B)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("KW" + Phase.B)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("RegTap" + Phase.B)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("RegVset" + Phase.B)).andStubReturn(" # ");

        EasyMock.expect(template.evaluateAsString("I" + Phase.C)).andStubReturn("1#");
        EasyMock.expect(template.evaluateAsString("VBase" + Phase.C)).andStubReturn("2#");
        EasyMock.expect(template.evaluateAsString("KVAR" + Phase.C)).andStubReturn("3#");
        EasyMock.expect(template.evaluateAsString("KW" + Phase.C)).andStubReturn("4#");
        EasyMock.expect(template.evaluateAsString("RegTap" + Phase.C)).andStubReturn("5#");
        EasyMock.expect(template.evaluateAsString("RegVset" + Phase.C)).andStubReturn("6#");

        EasyMock.expect(template.evaluateAsString("I" + Phase.ALL)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("VBase" + Phase.ALL)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("KVAR" + Phase.ALL)).andStubReturn("1#");
        EasyMock.expect(template.evaluateAsString("KW" + Phase.ALL)).andStubReturn("");
        EasyMock.expect(template.evaluateAsString("RegTap" + Phase.ALL)).andStubReturn(" # ");
        EasyMock.expect(template.evaluateAsString("RegVset" + Phase.ALL)).andStubReturn("2#");
        EasyMock.replay(template);

        assertTrue(ReflectionTestUtils.invokeMethod(helper, "getPhaseInformation", Phase.A, template) == null);

        assertTrue(ReflectionTestUtils.invokeMethod(helper, "getPhaseInformation", Phase.B, template) == null);

        assertTrue(ReflectionTestUtils.invokeMethod(helper, "getPhaseInformation", Phase.ALL, template) == null);

        PhaseInformation phaseInformationC = ReflectionTestUtils.invokeMethod(helper, "getPhaseInformation", Phase.C, template);
        assertTrue(phaseInformationC.getCurrent() == 1f);
        assertTrue(phaseInformationC.getVoltage() == 2f);
        assertTrue(phaseInformationC.getkVar() == 3f);
        assertTrue(phaseInformationC.getkW() == 4f);
        assertTrue(phaseInformationC.getTapPosition() == 5f);
        assertTrue(phaseInformationC.getVoltageSetPoint() == 6f);
    }
}
