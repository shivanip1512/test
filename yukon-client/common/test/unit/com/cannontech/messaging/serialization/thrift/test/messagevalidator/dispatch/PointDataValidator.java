package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class PointDataValidator extends AutoInitializedClassValidator<PointData> {

    private static final long DEFAULT_SEED = 12;

    public PointDataValidator() {
        super(PointData.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(PointData ctrlObj, RandomGenerator generator) {
        ctrlObj.setId(generator.generateInt());
        ctrlObj.setType(generator.generateEnumInt(PointType.class));
        ctrlObj.setPointQuality(generator.generateEnum(PointQuality.class));
        ctrlObj.setTags(generator.generateUIntAsLong());
        ctrlObj.setValue(generator.generateDouble());
        ctrlObj.setTime(generator.generateDate());
        ctrlObj.setMillis(generator.generateLong(0, 999));
        ctrlObj.setStr(generator.generateString());        
    }
}
