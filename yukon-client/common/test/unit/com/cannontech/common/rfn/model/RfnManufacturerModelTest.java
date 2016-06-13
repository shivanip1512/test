package com.cannontech.common.rfn.model;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class RfnManufacturerModelTest {
    
    @Test
    public void testGetForType() {
    	Assert.assertEquals(
    			RfnManufacturerModel.getForType(PaoType.RFN410CL), 
    			Lists.newArrayList(RfnManufacturerModel.RFN_410CL));
    	
    	Assert.assertEquals(
    			RfnManufacturerModel.getForType(PaoType.RFN530S4ERT), 
    			Lists.newArrayList(
    					RfnManufacturerModel.RFN_530S4RT,
    					RfnManufacturerModel.RFN_530S4RR));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetInvalidType() {
		RfnManufacturerModel.getForType(PaoType.MCT410CL);
    }
    
    @Test
    public void testOf() {
        
        RfnIdentifier rfnId;
        
        //  Manual good example
        rfnId = new RfnIdentifier("123456", "EE", "A3D");
        Assert.assertEquals(RfnManufacturerModel.RFN_430A3D, RfnManufacturerModel.of(rfnId));
        
        //  Manual bad examples
        rfnId = new RfnIdentifier("123456", "Larry", "CableGuy");
        Assert.assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier("123456", "LGYR", "FocuskWh");
        Assert.assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        //  Run through all of them by enum
        for (RfnManufacturerModel original : RfnManufacturerModel.values()) {
            rfnId = new RfnIdentifier("123456", original.getManufacturer(), original.getModel());
            RfnManufacturerModel resolved = RfnManufacturerModel.of(rfnId);
            //  The ambiguous cases will return null
            if (resolved != null) {
                Assert.assertEquals(original, RfnManufacturerModel.of(rfnId));
            }
        }
    }
    
    @Test
    public void testAmbiguousEnumEntries() {
        List<RfnManufacturerModel> ambiguousEntries = ImmutableList.of(
            RfnManufacturerModel.RFN_420FL,
            RfnManufacturerModel.RFN_420FX,
            RfnManufacturerModel.RFN_420FD,
            RfnManufacturerModel.RFN_410FL,
            RfnManufacturerModel.RFN_410FX_D,
            RfnManufacturerModel.RFN_410FD_D);       
        
        for (RfnManufacturerModel mm : ambiguousEntries) {
            Assert.assertEquals(null, RfnManufacturerModel.of(new RfnIdentifier("123456", mm.getManufacturer(), mm.getModel())));
        }
    }
    
    @Test
    public void testAmbiguousModelStrings() {
        for (String model : ImmutableList.of("FocuskWh", "FocusAXD", "FocusAXR-SD")) {
            Assert.assertEquals(null, RfnManufacturerModel.of(new RfnIdentifier("123456", "LGYR", model)));
        }
    }
}
