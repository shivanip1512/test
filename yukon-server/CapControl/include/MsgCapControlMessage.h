#pragma once

#include "message.h"

class CapControlMessage : public CtiMessage
{
    private:
        typedef CtiMessage Inherited;

    public:
        CapControlMessage();
        virtual ~CapControlMessage();

        CapControlMessage& operator=(const CapControlMessage& right);
};
