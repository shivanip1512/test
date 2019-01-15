
package com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignalStatusEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SignalStatusEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Queued"/>
 *     &lt;enumeration value="InProgress"/>
 *     &lt;enumeration value="ErrorUnknown"/>
 *     &lt;enumeration value="ErrorESIUnknown"/>
 *     &lt;enumeration value="ErrorESIUnreachable"/>
 *     &lt;enumeration value="ESINoData"/>
 *     &lt;enumeration value="ErrorTimeout"/>
 *     &lt;enumeration value="ESIRadioStatusUnknown"/>
 *     &lt;enumeration value="ConfigurationError"/>
 *     &lt;enumeration value="InstallCodeValidationError"/>
 *     &lt;enumeration value="ProvisionInProgress"/>
 *     &lt;enumeration value="InvalidState"/>
 *     &lt;enumeration value="Success"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SignalStatusEnumeration")
@XmlEnum
public enum SignalStatusEnumeration {

    @XmlEnumValue("Queued")
    QUEUED("Queued"),
    @XmlEnumValue("InProgress")
    IN_PROGRESS("InProgress"),
    @XmlEnumValue("ErrorUnknown")
    ERROR_UNKNOWN("ErrorUnknown"),
    @XmlEnumValue("ErrorESIUnknown")
    ERROR_ESI_UNKNOWN("ErrorESIUnknown"),
    @XmlEnumValue("ErrorESIUnreachable")
    ERROR_ESI_UNREACHABLE("ErrorESIUnreachable"),
    @XmlEnumValue("ESINoData")
    ESI_NO_DATA("ESINoData"),
    @XmlEnumValue("ErrorTimeout")
    ERROR_TIMEOUT("ErrorTimeout"),
    @XmlEnumValue("ESIRadioStatusUnknown")
    ESI_RADIO_STATUS_UNKNOWN("ESIRadioStatusUnknown"),
    @XmlEnumValue("ConfigurationError")
    CONFIGURATION_ERROR("ConfigurationError"),
    @XmlEnumValue("InstallCodeValidationError")
    INSTALL_CODE_VALIDATION_ERROR("InstallCodeValidationError"),
    @XmlEnumValue("ProvisionInProgress")
    PROVISION_IN_PROGRESS("ProvisionInProgress"),
    @XmlEnumValue("InvalidState")
    INVALID_STATE("InvalidState"),
    @XmlEnumValue("Success")
    SUCCESS("Success");
    private final String value;

    SignalStatusEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignalStatusEnumeration fromValue(String v) {
        for (SignalStatusEnumeration c: SignalStatusEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
