package com.cannontech.message.dispatch.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.point.PointType;

public class PointDataTest {

    @Test
    public void test_serialization() {
        PointData original = new PointData();
        
        LocalDateTime messageTime = LocalDateTime.of(2019, 5, 28, 11, 37);
        LocalDateTime dataTime = LocalDateTime.of(2019, 3, 14, 15, 9, 26);
        
        original.setId(42);
        original.setMillis(997);
        original.setPointQuality(PointQuality.Questionable);
        original.setPriority(19);
        original.setSoeTag(217);
        original.setSource("sourceville");
        original.setStr("I'm a little pointdata, short and stout");
        original.setTagsDataTimestampValid(true);
        original.setTagsLoadProfileData(true);
        original.setTagsPointMustArchive(true);
        original.setTime(new Date(messageTime.toInstant(ZoneOffset.UTC).toEpochMilli()));
        original.setTimeStamp(new Date(dataTime.toInstant(ZoneOffset.UTC).toEpochMilli()));
        original.setTrackingId("syzygy");
        original.setType(PointType.Analog.getPointTypeId());
        original.setUserName("Neo");
        original.setValue(1729);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(original);
        } catch (IOException e) {
            fail(e.toString());
        }
        
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            Object obj = ois.readObject();

            assertTrue(obj instanceof PointData);
            
            PointData reconstituted = (PointData)obj;
            
            assertEquals(original, obj);
            
            assertEquals(original.getSource(), reconstituted.getSource());
            assertEquals(original.getTrackingId(), reconstituted.getTrackingId());
        } catch (ClassNotFoundException|IOException e) {
            fail(e.toString());
        }
    }
}
