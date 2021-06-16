package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.LMEcobeeRestoreCommand;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class LMEcobeeRestoreCommandSerializer extends SimpleThriftSerializer
        implements ThriftByteDeserializer<LMEcobeeRestoreCommand> {

    private static final Logger log = YukonLogManager.getLogger(LMEcobeeRestoreCommandSerializer.class);

    @Override
    public LMEcobeeRestoreCommand fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEcobeeRestore();

        deserialize(msgBytes, entity);

        int groupId = entity.get_groupId();
        Instant restoreTime = new Instant(entity.get_restoreTime() * 1000);

        log.trace("Passes Restore command parameters: Group Id : {}, Restore Time: {}",
                  groupId, restoreTime);
        return new LMEcobeeRestoreCommand(groupId, restoreTime);

    }
}
