package com.cannontech.amr.rfn.message.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;


public class ChannelDataTest {
    
    /**
     * It is ok if this test fails someday, that just means Yukon can no longer receive 
     * messages with this serialization.
     */
    @Test
    public void testReceivingPre527Messages() {
        // the following was generated with:
        //      ChannelData channelData = new ChannelData();
        //      channelData.setChannelNumber(0);
        //      channelData.setStatus(ChannelDataStatus.OK);
        //      channelData.setUnitOfMeasure("Wh");
        //      channelData.setUnitOfMeasureModifiers(ImmutableSet.of("Quadrant 1", "Quadrant 2"));
        //      channelData.setValue(34543.4);
        //      ByteArrayOutputStream out = new ByteArrayOutputStream();
        //      ObjectOutputStream serializer = new ObjectOutputStream(out);
        //      serializer.writeObject(channelData);
        //      byte[] byteArray = out.toByteArray();
        //      System.out.println(Arrays.toString(byteArray));
        byte[] original =
            { -84, -19, 0, 5, 115, 114, 0, 47, 99, 111, 109, 46, 99, 97, 110, 110, 111, 110, 116,
                    101, 99, 104, 46, 97, 109, 114, 46, 114, 102, 110, 46, 109, 101, 115, 115, 97,
                    103, 101, 46, 114, 101, 97, 100, 46, 67, 104, 97, 110, 110, 101, 108, 68, 97,
                    116, 97, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 5, 73, 0, 13, 99, 104, 97, 110, 110,
                    101, 108, 78, 117, 109, 98, 101, 114, 68, 0, 5, 118, 97, 108, 117, 101, 76, 0,
                    6, 115, 116, 97, 116, 117, 115, 116, 0, 55, 76, 99, 111, 109, 47, 99, 97, 110,
                    110, 111, 110, 116, 101, 99, 104, 47, 97, 109, 114, 47, 114, 102, 110, 47, 109,
                    101, 115, 115, 97, 103, 101, 47, 114, 101, 97, 100, 47, 67, 104, 97, 110, 110,
                    101, 108, 68, 97, 116, 97, 83, 116, 97, 116, 117, 115, 59, 76, 0, 13, 117, 110,
                    105, 116, 79, 102, 77, 101, 97, 115, 117, 114, 101, 116, 0, 18, 76, 106, 97,
                    118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 22,
                    117, 110, 105, 116, 79, 102, 77, 101, 97, 115, 117, 114, 101, 77, 111, 100,
                    105, 102, 105, 101, 114, 115, 116, 0, 15, 76, 106, 97, 118, 97, 47, 117, 116,
                    105, 108, 47, 83, 101, 116, 59, 120, 112, 0, 0, 0, 0, 64, -32, -35, -20, -52,
                    -52, -52, -51, 126, 114, 0, 53, 99, 111, 109, 46, 99, 97, 110, 110, 111, 110,
                    116, 101, 99, 104, 46, 97, 109, 114, 46, 114, 102, 110, 46, 109, 101, 115, 115,
                    97, 103, 101, 46, 114, 101, 97, 100, 46, 67, 104, 97, 110, 110, 101, 108, 68,
                    97, 116, 97, 83, 116, 97, 116, 117, 115, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 120,
                    114, 0, 14, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 69, 110, 117, 109, 0,
                    0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 120, 112, 116, 0, 2, 79, 75, 116, 0, 2, 87, 104,
                    115, 114, 0, 53, 99, 111, 109, 46, 103, 111, 111, 103, 108, 101, 46, 99, 111,
                    109, 109, 111, 110, 46, 99, 111, 108, 108, 101, 99, 116, 46, 73, 109, 109, 117,
                    116, 97, 98, 108, 101, 83, 101, 116, 36, 83, 101, 114, 105, 97, 108, 105, 122,
                    101, 100, 70, 111, 114, 109, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 91, 0, 8, 101,
                    108, 101, 109, 101, 110, 116, 115, 116, 0, 19, 91, 76, 106, 97, 118, 97, 47,
                    108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 120, 112, 117, 114, 0,
                    19, 91, 76, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 79, 98, 106, 101, 99,
                    116, 59, -112, -50, 88, -97, 16, 115, 41, 108, 2, 0, 0, 120, 112, 0, 0, 0, 2,
                    116, 0, 10, 81, 117, 97, 100, 114, 97, 110, 116, 32, 49, 116, 0, 10, 81, 117,
                    97, 100, 114, 97, 110, 116, 32, 50 };
        
        ChannelData channelData = new ChannelData();
        channelData.setChannelNumber(0);
        channelData.setStatus(ChannelDataStatus.OK);
        channelData.setUnitOfMeasure("Wh");
        channelData.setUnitOfMeasureModifiers(ImmutableSet.of("Quadrant 1", "Quadrant 2"));
        channelData.setValue(34543.4);
        
        try {
            
            ByteArrayInputStream in = new ByteArrayInputStream(original);
            ObjectInputStream deserializer = new ObjectInputStream(in);
            ChannelData originalChannelData = (ChannelData) deserializer.readObject();
            assertEquals(channelData.getChannelNumber(), originalChannelData.getChannelNumber());
            assertEquals(channelData.getStatus(), originalChannelData.getStatus());
            assertEquals(channelData.getUnitOfMeasure(), originalChannelData.getUnitOfMeasure());
            assertEquals(channelData.getUnitOfMeasureModifiers(), originalChannelData.getUnitOfMeasureModifiers());
            assertEquals(channelData.getValue(), originalChannelData.getValue());
            // adding a field to ChannelData may cause the following to fail, 
            // but as long as the above passed, this might still be a valid test
            assertEquals(channelData, originalChannelData); // optional
        } catch (Exception e) {
            fail();
        }
    }
    
}
