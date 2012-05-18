package com.cannontech.billing.model;

import javax.transaction.NotSupportedException;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum CMEPUnitEnum {
    KWHREG(BuiltInAttribute.USAGE), // Meter dial or register readings for printing on monthly bill. 
    KVAHREG(null), // Meter dial or register readings for printing on monthly bill. 
    KVARHREG(null), // Meter dial or register readings for printing on monthly bill. 
    GASREG(null), // Meter dial or register readings for printing on monthly bill. 
    PULSE(null), // Direct meter pulse readings. 
    KW(null), // Kilowatt demand, usually expressed as peak value in time interval. 
    KVA(null), // Kilovolt-ampere demand, usually expressed as peak value in time interval. 
    KVAR(null), // Kilovolt-Ampere-Reactive demand, usually expressed as peak value in time interval. Values may be positive or negative depending upon power factor. Values are positive for VARs produced by customer or negative for VARs consumed by customer. Induction motors consume watts and VARs. A condenser bank produces VARs. An over-excited generator produces watts and VARs. An under-excited generator produces watts and consumes VARs. 
    KWH(BuiltInAttribute.LOAD_PROFILE), // Kilowatt hours used. 
    KVAH(null), // Kilovolt-ampere hours. 
    KVARH(null), // Kilovolt-Ampere-Reactive hours. Values may be positive or negative depending upon power factor. See KVAR above for further notes. 
    GKW(null), // Kilowatt generation, received from customer, usually expressed as peak value in time interval. Used when customer is generating power. 
    GKVA(null), // Kilovolt-Ampere generation received from customer, usually expressed as peak value in time interval. Used when customer is generating power. 
    GKVAR(null), // Kilovolt-Ampere Reactive generation received from customer, usually expressed as peak value in time interval. Used when customer is generating power. Values may be positive or negative depending upon power  factor. See KVAR above for further notes. 
    GKWH(null), // Kilowatt hours received from customer. Used when customer is generating power. 
    GKVAH(null), // Kilovolt-Ampere hours received from customer. Used when customer is generating power.
    GKVARH(null), // Kilovolt-Ampere Reactive hours received from customer. Used when customer is generating power. Values may be positive or negative depending upon power factor. See KVAR above for further notes. 
    VOLTS(null), // Volts. 
    BTU(null), // British Thermal Units. 
    THERM(null), // Therms. 
    GALREG(BuiltInAttribute.USAGE_WATER), // Gallons. 
    GAL(null), // Gallons. 
    CF(null), // Cubic Feet. 
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
    
    public Attribute getAttribute() throws NotSupportedException {
        if (attribute == null) {
            throw new NotSupportedException("The unit "+name()+" is not currently supported by Yukon");
        }
        return attribute;
    }
}