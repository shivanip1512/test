package com.cannontech.amr.rfn.message.status;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RfnStatusType {
    /**
     * A demand reset has occurred. There is no value associated with demand reset.
     * {@link RfnStatus#getTimeStamp()} indicates when the demand reset occurred.
     */
    DEMAND_RESET,
    /**
     * The value is a string that may contain one or more of the following fields related to the
     * sensor.
     * <ul>
     * <li>manufacturer - String containing the meter manufacturer.</li>
     * <li>model - String containing the meter model number.</li>
     * <li>sensorSN - String containing the meter serial number.</li>
     * <li>meterConfigID - String containing the meter configuration ID.</li>
     * <li>disconnectStatus - Hex string representing the 2-byte disconnect status
     * &lt;meter_mode&gt;&lt;relay_status&gt; (i.e. "FFFF").
     * <ul>
     * <li>&lt;meter_mode&gt; - 1 byte
     * <ul>
     * <li>00 - (Not used)</li>
     * <li>01 - Terminate (On-demand Disconnect)</li>
     * <li>02 - Arm (Legacy)</li>
     * <li>03 - Resume (On-demand Connect)</li>
     * <li>04 - (Not used)</li>
     * <li>05 - On-demand Configuration</li>
     * <li>06 - Demand Threshold Configuration</li>
     * <li>07 - Demand Threshold Activate</li>
     * <li>08 - Demand Threshold Deactivate</li>
     * <li>09 - Cycling Configuration</li>
     * <li>0A - Cycling Activate</li>
     * <li>0B - Cycling Deactivate</li>
     * </ul>
     * </li>
     * <li>&lt;relay_status&gt; - 1 byte
     * <ul>
     * <li>00 - Unknown</li>
     * <li>01 - Terminate</li>
     * <li>02 - Arm</li>
     * <li>03 - Resume</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * Format of the string:
     * 
     * <pre>
     * {@code 
     * manufacturer=<manufacturer>, model=<model>, sensorSN=<sensorSN>, meterConfigID=<meterConfigID>, disconnectStatus=<disconnectStatus>
     * }
     * </pre>
     */
    METER_INFO,
    ;

    // Field Name Constants
    /**
     * If an RfnStatusType has multiple fields, use these constants to access a specific field's
     * value. Example:
     * {@code
     *      if (RfnStatusType.METER_INFO == rfnStatus.getType())
     *          meterConfigID = RfnStatusType.getFields(rfnStatus.getValue())
     *              .get(RfnStatusType.METER_INFO_METER_CONFIG_ID);
     * }
     */

    /**
     * Applies to values for {@link #METER_INFO}. Field name for meter configuration ID.
     */
    public static String METER_INFO_METER_CONFIG_ID = "meterConfigID";
    /**
     * Applies to values for {@link #METER_INFO}. Field name for meter disconnect status.
     */
    public static String METER_INFO_DISCONNECT_STATUS = "disconnectStatus";

    /**
     * Parses a string into key/value pairs and returns them as entries in a map. A comma (",")
     * delimits the pairs, and an equals sign ("=") delimits the key/value.
     * <p>
     * Implementation notes:
     * <ul>
     * <li>If the string is null, an empty map is returned.</li>
     * <li>Any whitespace around the key or value is removed.</li>
     * <li>A key/value pair is omitted if the key is blank.</li>
     * <li>A key/value pair is omitted if the value is blank.</li>
     * <li>When a key/value contains multiple equals signs, only the first is considered a
     * delimiter.</li>
     * <li>If the string contains duplicate keys, only one it kept.</li>
     * </ul>
     * The following string: <br />
     * " , key1 = value1 ,keyOnly=,=valueOnly,=,,eqKey=val1=val2,dupKey = A , dupKey= B , "<br />
     * Will result a map containing the following entries:
     * <ul>
     * <li>"key1" => "value1"</li>
     * <li>"eqKey" => "val1=val2"</li>
     * <li>"dupKey" => "B"</li>
     * </ul>
     */
    public static Map<String, String> getFields(String value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        return Stream
            .of(value.split(","))
            .map(field -> field.split("=", 2))
            .filter(keyVal -> keyVal.length == 2 && !keyVal[0].isBlank() && !keyVal[1].isBlank())
            .collect(Collectors.toMap(kv -> kv[0].trim(), kv -> kv[1].trim(), (kv1, kv2) -> kv2));
    }
}
