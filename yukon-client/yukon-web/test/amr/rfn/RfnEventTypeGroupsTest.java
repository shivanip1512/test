package amr.rfn;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.amr.rfnEventsReport.model.RfnEventStatusTypeGroupings;

public class RfnEventTypeGroupsTest {
    
    @Test
    public void checkAllSets() {
        boolean result = true;
        try {
            checkSetAgainstAll(RfnEventStatusTypeGroupings.getGeneral());
            checkSetAgainstAll(RfnEventStatusTypeGroupings.getHardware());
            checkSetAgainstAll(RfnEventStatusTypeGroupings.getTamper());
            checkSetAgainstAll(RfnEventStatusTypeGroupings.getMetering());
            checkSetAgainstAll(RfnEventStatusTypeGroupings.getOutage());
            
            checkNoOverlap(RfnEventStatusTypeGroupings.getGeneral());
            checkNoOverlap(RfnEventStatusTypeGroupings.getHardware());
            checkNoOverlap(RfnEventStatusTypeGroupings.getTamper());
            checkNoOverlap(RfnEventStatusTypeGroupings.getMetering());
            checkNoOverlap(RfnEventStatusTypeGroupings.getOutage());
        } catch (IllegalArgumentException e) {
            result = false;
        }
        
        Assert.assertTrue(result);
    }
    
    private static void checkSetAgainstAll(Set<BuiltInAttribute> attrs) {
        for (BuiltInAttribute attr : attrs) {
            if (!RfnEventStatusTypeGroupings.getAllTypes().contains(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " not contained in ALL set");
            }
        }
    }
    
    private static void checkNoOverlap(Set<BuiltInAttribute> checkAgainst) {
        checkNoOverlap(checkAgainst, RfnEventStatusTypeGroupings.getGeneral());
        checkNoOverlap(checkAgainst, RfnEventStatusTypeGroupings.getHardware());
        checkNoOverlap(checkAgainst, RfnEventStatusTypeGroupings.getTamper());
        checkNoOverlap(checkAgainst, RfnEventStatusTypeGroupings.getMetering());
        checkNoOverlap(checkAgainst, RfnEventStatusTypeGroupings.getOutage());
    }
    
    private static void checkNoOverlap(Set<BuiltInAttribute> one, Set<BuiltInAttribute> two) {
        if (one == two) {
            return;
        }
        for (BuiltInAttribute attr : one) {
            if (two.contains(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " contained in more than one set");
            }
        }
    }
    
}
