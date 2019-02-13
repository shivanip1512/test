
package com.cannontech.dr.rfn.service.impl;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.dr.rfn.service.ParsingService;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.cannontech.dr.rfn.tlv.TlvTypeLength;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Tag-Length-Value parser. The TLV-format is used in LCR 6700
 * 
 * File Header
 * e2 01 70    -- Expresscom Message response 0xE2, 0x170 bytes to follow
 * 04 3e 00 04 -- SSPEC 1086, schema version 0.0.4
 * 
 * TLV format(TLV Report):
 * 
 * Type (1 byte + nibble (half byte)) -- It has a unique type associated with it
 * Length (nibble (Half byte) + 1 byte) -- Length in bytes of the value.
 * Value (3+) -- The value of the field
 * 
 * Example :- 00 d0 04 20 b8 6a 80 - Relay Interval Start time is 0x20B86A80
 * 
 * Type   -- 00d (Type ID)
 * Length -- 004 (4 Byte length of value)
 * Value  -- 0x20B86A80 (Value - 4 Byte)
 * 
 * The above format applies for each filed in TLV report except header part
 * 
 */
public class TLVParsingServiceImpl implements ParsingService<ListMultimap<FieldType, byte[]>> {
	private static final Logger rfnLogger = YukonLogManager.getRfnLogger();

    @Override
    public ListMultimap<FieldType, byte[]> parseRfLcrReading(RfnIdentifier rfnId, byte[] payload)
            throws ParseException {
        ListMultimap<FieldType, byte[]> listMultimap;

        try {
          
            byte[] data = Arrays.copyOf(payload, payload.length);
            if (data[0] == expresscomPayloadHeader) {
                // Trim 3-byte Expresscom header from the payload
                data = Arrays.copyOfRange(data, 3, data.length);
            }
            byte[] report = Arrays.copyOfRange(data, 4, data.length);
            listMultimap = parseTlvReport(report, rfnId);
            if (rfnLogger.isDebugEnabled()) {
                rfnLogger.debug("device: " + rfnId + " payload: " + listMultimap.keys());
            }

        } catch (Exception ex) {
            throw new ParseException("Error while parsing the RF LCR payload.", ex);
        }
        return listMultimap;
    }

    /**
     * Parses tlv report data
     */
    private ListMultimap<FieldType, byte[]> parseTlvReport(final byte[] report, RfnIdentifier rfnId) {
        ListMultimap<FieldType, byte[]> fieldTypeValues = ArrayListMultimap.create();

        for (int position = 0; position < report.length - 1;) {

            byte[] typeLengthBytes = Arrays.copyOfRange(report, position, position = position + 3);
            int type = TlvTypeLength.getType(typeLengthBytes[0], typeLengthBytes[1]);
            int length = TlvTypeLength.getLength(typeLengthBytes[1], typeLengthBytes[2]);
            
            if (FieldType.isFieldTypeSupported(type)) {
                final byte[] tlvValue = Arrays.copyOfRange(report, position, position += length);

                fieldTypeValues.put(FieldType.getFieldType(type), tlvValue);

            } else {
                position += length;
            }

        }

        return fieldTypeValues;
    }

}
