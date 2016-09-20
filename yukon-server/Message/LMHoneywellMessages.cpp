#include "precompiled.h"

#include "LMHoneywellMessages.h"
#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>


namespace Cti {
    namespace Messaging {
    namespace LoadManagement {

        LMHoneywellCyclingControlMessage::LMHoneywellCyclingControlMessage(int  groupId,
                                                                           int  dutyCycle,
                                                                           int  startTime,
                                                                           int  controlDuration,
                                                                           bool rampInOut)
            : 
            _groupId(groupId),
            _rampingOption(rampInOut),
            _dutyCycle(dutyCycle),
            _startTime(startTime),
            _stopTime(startTime + controlDuration)
        {
            // empty
        }

        void LMHoneywellCyclingControlMessage::streamInto(cms::StreamMessage & message) const
        {
            message.writeInt(_groupId);
            message.writeByte(_dutyCycle);
            message.writeByte(_rampingOption);
            message.writeInt(_startTime);
            message.writeInt(_stopTime);
        }

        LMHoneywellRestoreMessage::LMHoneywellRestoreMessage(int groupId,
                                                             int restoreTime)
            : 
            _groupId(groupId),
            _restoreTime(restoreTime)
        {
            // empty
        }

        void LMHoneywellRestoreMessage::streamInto(cms::StreamMessage & message) const
        {
            message.writeInt(_groupId);
            message.writeInt(_restoreTime);
        }

    }
    }
}

