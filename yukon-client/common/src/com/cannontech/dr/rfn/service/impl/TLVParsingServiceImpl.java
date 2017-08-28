
package com.cannontech.dr.rfn.service.impl;

import java.util.Arrays;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.YukonLogManager.RfnLogger;
import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.dr.rfn.service.ParsingService;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Tag-Length-Value parser. The TLV-format is used in LCR 6700
 * 
 */
public class TLVParsingServiceImpl implements ParsingService<ListMultimap<FieldType, byte[]>> {
    private static final RfnLogger rfnLogger = YukonLogManager.getRfnLogger();

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

    private static ListMultimap<FieldType, byte[]> parseTlvReport(final byte[] report, RfnIdentifier rfnId) {
        ListMultimap<FieldType, byte[]> fieldTypeValues = ArrayListMultimap.create();

        for (int postion = 0; postion < report.length - 1;) {

            byte[] typeLenghtBytes = Arrays.copyOfRange(report, postion, postion = postion + 3);
            final byte upperTypeByte = typeLenghtBytes[0];

            byte middleTypeLenghtByte = typeLenghtBytes[1];
            byte upperLengthNibble = (byte) (middleTypeLenghtByte & 0xf);
            byte lowerTypeNibble = (byte) ((middleTypeLenghtByte >> 4) & 0xf);

            final int type = ((upperTypeByte & 0xff) << 4) | (lowerTypeNibble & 0xf);
            byte lowerLenghtByte = typeLenghtBytes[2];

            final int length = (lowerLenghtByte & 0xff) | ((upperLengthNibble & 0xf) << 12);

            if (FieldType.isFieldTypeSupported(type)) {
                final byte[] tlvValue = Arrays.copyOfRange(report, postion, postion += length);

                fieldTypeValues.put(FieldType.getFieldType(type), tlvValue);

            } else {
                postion += length;
            }

        }

        return fieldTypeValues;
    }

}
