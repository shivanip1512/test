package com.cannontech.billing.model;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;

public enum CMEPUnitEnum {
    KWHREG(BuiltInAttribute.USAGE), // Meter dial or register readings for printing on monthly bill. 
    KVAHREG(null), // Meter dial or register readings for printing on monthly bill. 
    KVARHREG(null), // Meter dial or register readings for printing on monthly bill. 
    GASREG(null), // Meter dial or register readings for printing on monthly bill. 
    PULSE(null), // Direct meter pulse readings. 
    KW(BuiltInAttribute.PEAK_DEMAND), // Kilowatt demand, usually expressed as peak value in time interval. 
    KVA(null), // Kilovolt-ampere demand, usually expressed as peak value in time interval. 
    KVAR(BuiltInAttribute.PEAK_KVAR), // Kilovolt-Ampere-Reactive demand, usually expressed as peak value in time interval. Values may be positive or negative depending upon power factor. Values are positive for VARs produced by customer or negative for VARs consumed by customer. Induction motors consume watts and VARs. A condenser bank produces VARs. An over-excited generator produces watts and VARs. An under-excited generator produces watts and consumes VARs. 
    KWH(BuiltInAttribute.LOAD_PROFILE), // Kilowatt hours used.  // Using LOAD_PROFILE for this value is a little unusual, 
                                                             // but since we are using this in correlation with the calculation constraint value it creates a KWH value.
    KVAH(null), // Kilovolt-ampere hours. 
    KVARH(BuiltInAttribute.KVARH), // Kilovolt-Ampere-Reactive hours. Values may be positive or negative depending upon power factor. See KVAR above for further notes. 
    GKW(null), // Kilowatt generation, received from customer, usually expressed as peak value in time interval. Used when customer is generating power. 
    GKVA(null), // Kilovolt-Ampere generation received from customer, usually expressed as peak value in time interval. Used when customer is generating power. 
    GKVAR(null), // Kilovolt-Ampere Reactive generation received from customer, usually expressed as peak value in time interval. Used when customer is generating power. Values may be positive or negative depending upon power  factor. See KVAR above for further notes. 
    GKWH(BuiltInAttribute.RECEIVED_KWH), // Kilowatt hours received from customer. Used when customer is generating power. 
    GKVAH(null), // Kilovolt-Ampere hours received from customer. Used when customer is generating power.
    GKVARH(null), // Kilovolt-Ampere Reactive hours received from customer. Used when customer is generating power. Values may be positive or negative depending upon power factor. See KVAR above for further notes. 
    VOLTS(null), // Volts. 
    BTU(null), // British Thermal Units. 
    THERM(null), // Therms. 
    GALREG(BuiltInAttribute.USAGE_WATER), // Gallons. 
    GAL(null), // Gallons. 
    CF(BuiltInAttribute.USAGE_GAS), // Cubic Feet. 
    CCF(null), // Hundreds of Cubic Feet. 
    MCF(null), // Thousands of Cubic Feet. 
    ACFT(null), // Acre-Feet. 
    LBS(null), // Pounds. 
    $(null), // US Dollars.
    ;
    
    private Attribute attribute;
    
    private CMEPUnitEnum(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public Attribute getAttribute() throws IllegalUseOfAttribute {
        if (attribute == null) {
            throw new IllegalUseOfAttribute("The unit "+name()+" is not currently supported by Yukon");
        }
        return attribute;
    }
}