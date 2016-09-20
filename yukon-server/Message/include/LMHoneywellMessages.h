#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
    namespace Messaging {
    namespace LoadManagement {

        class IM_EX_MSG LMHoneywellCyclingControlMessage : public StreamableMessage
        {
        public:

            LMHoneywellCyclingControlMessage(int  groupId,
                                             int  dutyCycle,
                                             int  startTime,
                                             int  controlDuration,
                                             bool rampInOut);

            void streamInto(cms::StreamMessage & message) const;

        private:

            int  _groupId;
            char _rampingOption;
            char _dutyCycle;
            int  _startTime;
            int  _stopTime;
        };

        class IM_EX_MSG LMHoneywellRestoreMessage : public StreamableMessage
        {
        public:

            LMHoneywellRestoreMessage(int groupId,
                                      int restoreTime);

            void streamInto(cms::StreamMessage & message) const;

        private:

            int _groupId;
            int _restoreTime;
        };

    }
    }
}

