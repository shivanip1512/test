package com.cannontech.dr.rfn.service;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.google.common.collect.ImmutableMap;

public interface ParsingService<T> {
    final static byte expresscomPayloadHeader = (byte) 0xE2;

    public enum Schema {
        SCHEMA_0_0_2("0.0.2", "rfnLcrExiMessageSchema_v0_0_2.xsd"),
        SCHEMA_0_0_3("0.0.3", "rfnLcrExiMessageSchema_v0_0_3.xsd"),
        SCHEMA_0_0_4("0.0.4", "null");

        private final String version;
        private final String schema;
        private static final String classpath = "classpath:com/cannontech/dr/rfn/endpoint/";
        private static final Map<String, Schema> lookupByVersion;

        static {
            ImmutableMap.Builder<String, Schema> builder = ImmutableMap.builder();
            for (Schema schema : values()) {
                builder.put(schema.getVersion(), schema);
            }
            lookupByVersion = builder.build();
        }

        private Schema(String version, String schema) {
            this.version = version;
            this.schema = schema;
        }

        public String getVersion() {
            return version;
        }

        public String getLocation() {
            return classpath + schema;
        }

        public boolean supportsBroadcastVerificationMessages() {
            return (this == SCHEMA_0_0_3 || this == SCHEMA_0_0_4);
        }

        public static Schema getSchema(String schemaVersion) {
            return lookupByVersion.get(schemaVersion);
        }
    }

    /**
     * This method takes an encoded message as a byte array and converts it
     * into a SimpleXPathTemplate or Map which can be queried using XPath statements or Using TLV Field ID.
     * 
     * @param rfId The rfnIdentifier of the device whose payload is being decoded.
     * @param payload The encoded message as a byte array.
     * @throws ParseException If payload cannot be properly expanded.
     */

    public T parseRfLcrReading(RfnIdentifier rfnId, byte[] payload) throws ParseException;

    /**
     * Parse the schema version from the header and return a schema
     */
    public static Schema getSchema(byte[] payload) {
        ByteBuffer header;
        if (payload[0] == expresscomPayloadHeader) {
            header = ByteBuffer.wrap(Arrays.copyOfRange(payload, 3, 8));
        } else {
            header = ByteBuffer.wrap(Arrays.copyOfRange(payload, 0, 4));
        }
        StringBuilder version = new StringBuilder();
        ByteBuffer majorMinorBuffer = ByteBuffer.allocate(1);
        majorMinorBuffer.put(header.array(), 2, 1);
        // majorMinorBuffer.put((byte) 0xEA); to test
        int major = (majorMinorBuffer.get(0) & 0xF0) >>> 4;
        int minor = majorMinorBuffer.get(0) & 0x0F;
        version.append(major);
        version.append(".");
        version.append(minor);
        version.append(".");
        ByteBuffer revisionBuffer = ByteBuffer.allocate(1);
        revisionBuffer.put(header.array(), 3, 1);
        version.append(revisionBuffer.get(0));
        return Schema.getSchema(version.toString());
    }

}