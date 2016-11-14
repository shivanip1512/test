#pragma once

#include "dlldefs.h"
#include "logger.h"

#include <string> 

using std::string;

namespace Cti {
class MessageCounter
{
    string counterName;

    unsigned long messageCount = 0;
    unsigned long messageLog = 0;

public:
    MessageCounter(string counterName)
    {
        this->counterName = counterName;
    }

    void tick(int count=1)
    {
        messageCount++;
        messageLog++;

        if (messageLog >= 1000)
        {
            messageLog = 0;
            CTILOG_INFO(dout, counterName << " has processed " << messageCount << " messages");
        }
    }
};

}
