package com.cannontech.common.rfn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class RfnManufacturerModelTest {

    //  This is a list of models whose Manufacturer+Model combination were taken by a subsequent meter type. 
    private static Map<RfnManufacturerModel, RfnManufacturerModel> overriddenModels = 
            ImmutableMap.of(
                RfnManufacturerModel.RFN_410FL, RfnManufacturerModel.RFN_420FL,
                RfnManufacturerModel.RFN_410FX_D, RfnManufacturerModel.RFN_420FX,
                RfnManufacturerModel.RFN_410FD_D, RfnManufacturerModel.RFN_420FD);
    
    @Test
    public void testGetForType() {
    	assertEquals(
    			RfnManufacturerModel.getForType(PaoType.RFN410CL), 
    			ImmutableList.of(RfnManufacturerModel.RFN_410CL));
    	
    	assertEquals(
    			RfnManufacturerModel.getForType(PaoType.RFN530S4ERXR), 
    			ImmutableList.of(
    					RfnManufacturerModel.RFN_530S4RT,
    					RfnManufacturerModel.RFN_530S4RR));

        assertEquals(
                RfnManufacturerModel.getForType(PaoType.RFN530FAX), 
                ImmutableList.of(
                         RfnManufacturerModel.RFN_530FAXD,
                         RfnManufacturerModel.RFN_530FAXT,
                         RfnManufacturerModel.RFN_530FAXR));

        assertEquals(
                RfnManufacturerModel.getForType(PaoType.RFN530FRX), 
                ImmutableList.of(
                         RfnManufacturerModel.RFN_530FRXD,
                         RfnManufacturerModel.RFN_530FRXT,
                         RfnManufacturerModel.RFN_530FRXR));
    }
    
    @Test
    public void testGetInvalidType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RfnManufacturerModel.getForType(PaoType.MCT410CL);
        });
    }
    
    @Test
    public void testOf() {
        
        RfnIdentifier rfnId;
        
        //  Manual good example
        rfnId = new RfnIdentifier("123456", "EE", "A3D");
        assertEquals(RfnManufacturerModel.RFN_430A3D, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier("", "EE", "A3D");
        assertEquals(RfnManufacturerModel.RFN_430A3D, RfnManufacturerModel.of(rfnId));

        rfnId = new RfnIdentifier(null, "EE", "A3D");
        assertEquals(RfnManufacturerModel.RFN_430A3D, RfnManufacturerModel.of(rfnId));
        
        //  Manual bad examples
        rfnId = new RfnIdentifier("123456", "Larry", "CableGuy");
        assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier("123456", "", "CableGuy");
        assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier("123456", "Larry", "");
        assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier("", "", "");
        assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        rfnId = new RfnIdentifier(null, null, null);
        assertEquals(null, RfnManufacturerModel.of(rfnId));
        
        //  Run through all of them by enum
        for (RfnManufacturerModel original : RfnManufacturerModel.values()) {
            rfnId = new RfnIdentifier("123456", original.getManufacturer(), original.getModel());

            if (overriddenModels.containsKey(original)) {
                RfnManufacturerModel override = overriddenModels.get(original);

                //  If this model is deprecated/overridden, the reconstituted RfnManufacturerModel should match the override
                assertEquals(override, RfnManufacturerModel.of(rfnId));
            } else {
                //  Otherwise, the reconstituted RfnManufacturerModel should match the original
                assertEquals(original, RfnManufacturerModel.of(rfnId));
            }
        }
    }
}
