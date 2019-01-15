
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HANDeviceClassEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HANDeviceClassEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="HVAC"/>
 *     &lt;enumeration value="StripHeater"/>
 *     &lt;enumeration value="WaterHeater"/>
 *     &lt;enumeration value="PoolPump"/>
 *     &lt;enumeration value="SmartAppliance"/>
 *     &lt;enumeration value="IrrigationPump"/>
 *     &lt;enumeration value="CommercialIndustrialLoad"/>
 *     &lt;enumeration value="SimpleLoad"/>
 *     &lt;enumeration value="ExteriorLighting"/>
 *     &lt;enumeration value="InteriorLighting"/>
 *     &lt;enumeration value="ElectricVehicle"/>
 *     &lt;enumeration value="GenerationSystem"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HANDeviceClassEnumeration")
@XmlEnum
public enum HANDeviceClassEnumeration {

    HVAC("HVAC"),
    @XmlEnumValue("StripHeater")
    STRIP_HEATER("StripHeater"),
    @XmlEnumValue("WaterHeater")
    WATER_HEATER("WaterHeater"),
    @XmlEnumValue("PoolPump")
    POOL_PUMP("PoolPump"),
    @XmlEnumValue("SmartAppliance")
    SMART_APPLIANCE("SmartAppliance"),
    @XmlEnumValue("IrrigationPump")
    IRRIGATION_PUMP("IrrigationPump"),
    @XmlEnumValue("CommercialIndustrialLoad")
    COMMERCIAL_INDUSTRIAL_LOAD("CommercialIndustrialLoad"),
    @XmlEnumValue("SimpleLoad")
    SIMPLE_LOAD("SimpleLoad"),
    @XmlEnumValue("ExteriorLighting")
    EXTERIOR_LIGHTING("ExteriorLighting"),
    @XmlEnumValue("InteriorLighting")
    INTERIOR_LIGHTING("InteriorLighting"),
    @XmlEnumValue("ElectricVehicle")
    ELECTRIC_VEHICLE("ElectricVehicle"),
    @XmlEnumValue("GenerationSystem")
    GENERATION_SYSTEM("GenerationSystem");
    private final String value;

    HANDeviceClassEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HANDeviceClassEnumeration fromValue(String v) {
        for (HANDeviceClassEnumeration c: HANDeviceClassEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
